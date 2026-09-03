package deadlines.platform.onboarding.application

import deadlines.core.error.AppError
import deadlines.core.id.Ids
import deadlines.platform.access.domain.GrantedPermission
import deadlines.platform.access.domain.PermissionRepository
import deadlines.platform.access.domain.Role
import deadlines.platform.access.domain.RoleRepository
import deadlines.platform.access.domain.Scope
import deadlines.platform.access.domain.SystemRole
import deadlines.platform.accounts.domain.Slug
import deadlines.platform.accounts.domain.Tenant
import deadlines.platform.accounts.domain.TenantRepository
import deadlines.platform.accounts.domain.TenantStatus
import deadlines.platform.accounts.domain.TenantUser
import deadlines.platform.accounts.domain.TenantUserRepository
import deadlines.platform.accounts.domain.TenantUserStatus
import deadlines.platform.billing.domain.Subscription
import deadlines.platform.billing.domain.SubscriptionRepository
import deadlines.platform.billing.domain.SubscriptionStatus
import deadlines.platform.identity.application.PasswordHasher
import deadlines.platform.identity.domain.Email
import deadlines.platform.identity.domain.RawPassword
import deadlines.platform.identity.domain.User
import deadlines.platform.identity.domain.UserRepository
import deadlines.platform.persistence.application.TransactionRunner
import kotlinx.datetime.Clock
import java.util.UUID
import kotlin.time.Duration.Companion.days

private const val TRIAL_DAYS = 14
private const val MAX_SLUG_ATTEMPTS = 50

/** Creates a brand new account: the company, its first user, the system roles and a trial subscription. */
class SignUp(
    private val tenants: TenantRepository,
    private val tenantUsers: TenantUserRepository,
    private val users: UserRepository,
    private val roles: RoleRepository,
    private val permissions: PermissionRepository,
    private val subscriptions: SubscriptionRepository,
    private val passwordHasher: PasswordHasher,
    private val transactionRunner: TransactionRunner,
    private val clock: Clock = Clock.System,
) {
    suspend operator fun invoke(command: SignUpCommand): SignUpResult {
        val companyName = command.companyName.trim()
        val name = command.name.trim()
        val email = Email.of(command.email)
        val password = RawPassword.of(command.password)

        val violations =
            buildList {
                if (companyName.isEmpty()) add(AppError.Violation("companyName", "must not be empty"))
                if (name.isEmpty()) add(AppError.Violation("name", "must not be empty"))
                if (email == null) add(AppError.Violation("email", "must be a valid e-mail address"))
                if (password == null) {
                    add(
                        AppError.Violation(
                            "password",
                            "must have between ${RawPassword.MINIMUM_LENGTH} and ${RawPassword.MAXIMUM_LENGTH} characters",
                        ),
                    )
                }
            }
        if (violations.isNotEmpty()) throw AppError.Validation(violations)
        requireNotNull(email)
        requireNotNull(password)

        val passwordHash = passwordHasher.hash(password)

        return transactionRunner.transaction {
            if (users.findByEmail(email) != null) {
                throw AppError.Conflict("An account already exists for ${email.value}")
            }

            val tenant =
                tenants.create(
                    Tenant(
                        id = Ids.next(),
                        name = companyName,
                        slug = availableSlug(companyName),
                        status = TenantStatus.ACTIVE,
                    ),
                )
            val user =
                users.create(
                    User(id = Ids.next(), email = email, passwordHash = passwordHash, name = name),
                )
            val tenantUser =
                tenantUsers.create(
                    TenantUser(
                        id = Ids.next(),
                        tenantId = tenant.id,
                        userId = user.id,
                        status = TenantUserStatus.ACTIVE,
                    ),
                )

            createSystemRoles(tenant.id, tenantUser.id)

            subscriptions.create(
                Subscription(
                    id = Ids.next(),
                    tenantId = tenant.id,
                    status = SubscriptionStatus.TRIALING,
                    trialEndsAt = clock.now().plus(TRIAL_DAYS.days),
                ),
            )

            SignUpResult(
                tenantId = tenant.id,
                tenantSlug = tenant.slug.value,
                userId = user.id,
                email = user.email.value,
            )
        }
    }

    private suspend fun availableSlug(companyName: String): Slug {
        val base = Slug.from(companyName)
        if (!tenants.existsBySlug(base)) return base
        for (suffix in 2..MAX_SLUG_ATTEMPTS) {
            val candidate = base.withSuffix(suffix)
            if (!tenants.existsBySlug(candidate)) return candidate
        }
        return base.withSuffix(Ids.next().hashCode().and(Int.MAX_VALUE))
    }

    private suspend fun createSystemRoles(tenantId: UUID, ownerTenantUserId: UUID) {
        val catalogue = permissions.all().map { GrantedPermission(it.key, Scope.ALL) }
        SystemRole.entries.forEach { systemRole ->
            val role =
                roles.create(
                    Role(
                        id = Ids.next(),
                        tenantId = tenantId,
                        key = systemRole.key,
                        name = systemRole.displayName,
                        isSystem = true,
                    ),
                )
            if (systemRole == SystemRole.OWNER) {
                roles.grant(role.id, catalogue)
                roles.assign(ownerTenantUserId, role.id)
            }
        }
    }
}
