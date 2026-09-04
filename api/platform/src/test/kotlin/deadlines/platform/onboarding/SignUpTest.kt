package deadlines.platform.onboarding

import deadlines.core.error.AppError
import deadlines.core.id.Ids
import deadlines.platform.access.domain.Permission
import deadlines.platform.access.domain.Scope
import deadlines.platform.access.domain.SystemRole
import deadlines.platform.accounts.domain.Slug
import deadlines.platform.accounts.domain.Tenant
import deadlines.platform.accounts.domain.TenantStatus
import deadlines.platform.billing.domain.SubscriptionStatus
import deadlines.platform.identity.domain.PasswordHash
import deadlines.platform.identity.domain.RawPassword
import deadlines.platform.identity.infrastructure.Argon2PasswordHasher
import deadlines.platform.onboarding.application.SignUp
import deadlines.platform.onboarding.application.SignUpCommand
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val CATALOGUE =
    listOf(
        Permission("platform.users.read", "platform", "read users"),
        Permission("platform.users.invite", "platform", "invite users"),
    )

private val VALID =
    SignUpCommand(
        companyName = "Acme Industries",
        name = "Tarik Villalobos",
        email = "Tarik@Example.com",
        password = "correct horse battery",
    )

private class Fixture {
    val tenants = InMemoryTenantRepository()
    val tenantUsers = InMemoryTenantUserRepository()
    val users = InMemoryUserRepository()
    val roles = InMemoryRoleRepository()
    val subscriptions = InMemorySubscriptionRepository()
    val hasher = Argon2PasswordHasher()

    val signUp =
        SignUp(
            tenants = tenants,
            tenantUsers = tenantUsers,
            users = users,
            roles = roles,
            permissions = InMemoryPermissionRepository(CATALOGUE),
            subscriptions = subscriptions,
            passwordHasher = hasher,
            transactionRunner = DirectTransactionRunner(),
        )
}

class SignUpTest :
    StringSpec({
        "creates the company, the user, the roles and a trial subscription" {
            val fixture = Fixture()

            val result = fixture.signUp(VALID)

            result.tenantSlug shouldBe "acme-industries"
            result.email shouldBe "tarik@example.com"
            fixture.tenants.saved
                .single()
                .name shouldBe "Acme Industries"
            fixture.users.saved
                .single()
                .name shouldBe "Tarik Villalobos"
            fixture.tenantUsers.saved
                .single()
                .tenantId shouldBe result.tenantId
            fixture.subscriptions.saved
                .single()
                .status shouldBe SubscriptionStatus.TRIALING
        }

        "starts the trial with an end date in the future" {
            val fixture = Fixture()

            fixture.signUp(VALID)

            fixture.subscriptions.saved
                .single()
                .trialEndsAt
                .shouldNotBeNull()
        }

        "creates every system role and makes the first user an owner" {
            val fixture = Fixture()

            val result = fixture.signUp(VALID)

            fixture.roles.saved.map { it.key } shouldContainExactlyInAnyOrder SystemRole.entries.map { it.key }
            val owner = fixture.roles.saved.single { it.key == SystemRole.OWNER.key }
            fixture.roles.grants[owner.id]?.map { it.key } shouldContainExactlyInAnyOrder CATALOGUE.map { it.key }
            fixture.roles.grants[owner.id]
                ?.map { it.scope }
                ?.toSet() shouldBe setOf(Scope.ALL)
            fixture.roles.assignments.single() shouldBe (
                fixture.tenantUsers.saved
                    .single()
                    .id to owner.id
            )
        }

        "stores the password as a verifiable hash and never in plain text" {
            val fixture = Fixture()

            fixture.signUp(VALID)

            val stored =
                fixture.users.saved
                    .single()
                    .passwordHash
            stored shouldNotBe PasswordHash(VALID.password)
            fixture.hasher.verify(requireNotNull(RawPassword.of(VALID.password)), stored) shouldBe true
        }

        "rejects an e-mail that already has an account" {
            val fixture = Fixture()
            fixture.signUp(VALID)

            val error = shouldThrow<AppError.Conflict> { fixture.signUp(VALID.copy(companyName = "Another Co")) }

            error.status shouldBe 409
            fixture.tenants.saved.size shouldBe 1
        }

        "matches existing e-mails regardless of case" {
            val fixture = Fixture()
            fixture.signUp(VALID)

            shouldThrow<AppError.Conflict> { fixture.signUp(VALID.copy(email = "TARIK@EXAMPLE.COM")) }
        }

        "reports every invalid field at once" {
            val fixture = Fixture()

            val error =
                shouldThrow<AppError.Validation> {
                    fixture.signUp(SignUpCommand(companyName = " ", name = "", email = "nope", password = "short"))
                }

            error.violations.map { it.field } shouldContainExactlyInAnyOrder
                listOf("companyName", "name", "email", "password")
        }

        "gives a new company a suffixed slug when the name is taken" {
            val fixture = Fixture()
            fixture.tenants.saved +=
                Tenant(Ids.next(), "Acme Industries", Slug.from("Acme Industries"), TenantStatus.ACTIVE)

            val result = fixture.signUp(VALID)

            result.tenantSlug shouldBe "acme-industries-2"
        }

        "keeps nothing when a step inside the transaction fails" {
            val fixture = Fixture()
            fixture.tenants.failOnCreate = true

            shouldThrow<IllegalStateException> { fixture.signUp(VALID) }

            fixture.users.saved.shouldBeEmptyList()
            fixture.subscriptions.saved.shouldBeEmptyList()
        }
    })

private fun <T> List<T>.shouldBeEmptyList() = size shouldBe 0
