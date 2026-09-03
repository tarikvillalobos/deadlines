package deadlines.app.routes

import deadlines.app.module
import deadlines.app.support.IntegrationSpec
import deadlines.contracts.auth.SignUpRequest
import deadlines.contracts.auth.SignUpResponse
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication

private fun ApplicationTestBuilder.jsonClient() =
    createClient {
        install(ContentNegotiation) { json() }
    }

private suspend fun HttpClient.signUp(request: SignUpRequest): HttpResponse =
    post("/api/auth/signup") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }

private fun request(
    companyName: String = "Acme Industries",
    name: String = "Tarik Villalobos",
    email: String = "owner@acme.test",
    password: String = "correct horse battery",
) = SignUpRequest(companyName, name, email, password)

class SignUpRoutesTest :
    IntegrationSpec({
        fun freshEmail() = "owner-${System.nanoTime()}@acme.test"

        "creates an account and answers 201 with its identifiers" {
            testApplication {
                application { module(modules) }
                val client = jsonClient()

                val response = client.signUp(request(email = freshEmail()))

                response.status shouldBe HttpStatusCode.Created
                val body = response.body<SignUpResponse>()
                body.tenantSlug shouldContain "acme-industries"
                body.tenantId shouldNotBe ""
                body.userId shouldNotBe ""
            }
        }

        "persists the company, the membership, the roles and a trial subscription" {
            testApplication {
                application { module(modules) }
                val client = jsonClient()
                val email = freshEmail()

                val body = client.signUp(request(email = email)).body<SignUpResponse>()

                queryRows("SELECT status FROM tenants WHERE id = '${body.tenantId}'") { it.getString(1) }
                    .single() shouldBe "ACTIVE"
                queryRows("SELECT status FROM tenant_users WHERE tenant_id = '${body.tenantId}'") { it.getString(1) }
                    .single() shouldBe "ACTIVE"
                queryRows("SELECT key FROM roles WHERE tenant_id = '${body.tenantId}'") { it.getString(1) }
                    .shouldContainExactlyInAnyOrder(listOf("owner", "admin", "member"))
                queryRows("SELECT status FROM subscriptions WHERE tenant_id = '${body.tenantId}'") { it.getString(1) }
                    .single() shouldBe "TRIALING"
            }
        }

        "grants the owner every permission in the catalogue" {
            testApplication {
                application { module(modules) }
                val client = jsonClient()

                val body = client.signUp(request(email = freshEmail())).body<SignUpResponse>()

                val granted =
                    queryRows(
                        """
                        SELECT rp.scope FROM role_permissions rp
                        JOIN roles r ON r.id = rp.role_id
                        WHERE r.tenant_id = '${body.tenantId}' AND r.key = 'owner'
                        """.trimIndent(),
                    ) { it.getString(1) }
                val catalogue = queryRows("SELECT count(*) FROM permissions") { it.getInt(1) }.single()

                granted.size shouldBe catalogue
                granted.toSet() shouldBe setOf("ALL")
            }
        }

        "never stores the password in readable form" {
            testApplication {
                application { module(modules) }
                val client = jsonClient()
                val email = freshEmail()

                client.signUp(request(email = email))

                val hash =
                    queryRows("SELECT password_hash FROM users WHERE email = '$email'") { it.getString(1) }.single()
                hash shouldContain "\$argon2id\$"
                hash shouldNotContain "correct horse battery"
            }
        }

        "answers 409 when the e-mail already has an account" {
            testApplication {
                application { module(modules) }
                val client = jsonClient()
                val email = freshEmail()
                client.signUp(request(email = email))

                val response = client.signUp(request(companyName = "Another Co", email = email))

                response.status shouldBe HttpStatusCode.Conflict
                response.contentType()?.withoutParameters() shouldBe ContentType.Application.ProblemJson
                response.bodyAsText() shouldContain "urn:deadlines:problem:conflict"
            }
        }

        "answers 422 listing every invalid field" {
            testApplication {
                application { module(modules) }
                val client = jsonClient()

                val response = client.signUp(request(companyName = " ", name = "", email = "nope", password = "short"))

                response.status shouldBe HttpStatusCode.UnprocessableEntity
                val body = response.bodyAsText()
                body shouldContain "\"field\":\"companyName\""
                body shouldContain "\"field\":\"name\""
                body shouldContain "\"field\":\"email\""
                body shouldContain "\"field\":\"password\""
            }
        }

        "answers 400 when the body is not a valid payload" {
            testApplication {
                application { module(modules) }

                val response =
                    client.post("/api/auth/signup") {
                        contentType(ContentType.Application.Json)
                        setBody("{\"companyName\":\"Acme\"}")
                    }

                response.status shouldBe HttpStatusCode.BadRequest
                response.contentType()?.withoutParameters() shouldBe ContentType.Application.ProblemJson
            }
        }

        "gives the second company with the same name a distinct slug" {
            testApplication {
                application { module(modules) }
                val client = jsonClient()
                val shared = "Repeated Name ${System.nanoTime()}"

                val first = client.signUp(request(companyName = shared, email = freshEmail())).body<SignUpResponse>()
                val second = client.signUp(request(companyName = shared, email = freshEmail())).body<SignUpResponse>()

                second.tenantSlug shouldBe "${first.tenantSlug}-2"
            }
        }
    })
