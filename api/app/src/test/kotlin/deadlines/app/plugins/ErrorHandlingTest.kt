package deadlines.app.plugins

import deadlines.app.module
import deadlines.app.support.fakeDependencies
import deadlines.core.error.AppError
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication

class ErrorHandlingTest :
    StringSpec({
        "AppError becomes a problem+json response with its status" {
            testApplication {
                application {
                    module(fakeDependencies())
                    routing { get("/missing") { throw AppError.NotFound("Lead 42 does not exist") } }
                }

                val response = client.get("/missing")

                response.status shouldBe HttpStatusCode.NotFound
                response.contentType()?.withoutParameters() shouldBe ContentType.Application.ProblemJson
                response.bodyAsText() shouldBe
                    """{"type":"urn:deadlines:problem:not-found","title":"Resource not found","status":404,"detail":"Lead 42 does not exist"}"""
            }
        }

        "validation errors list every field violation" {
            testApplication {
                application {
                    module(fakeDependencies())
                    routing {
                        get("/invalid") {
                            throw AppError.Validation(
                                listOf(
                                    AppError.Violation("email", "must be a valid e-mail"),
                                    AppError.Violation("password", "must have at least 8 characters"),
                                ),
                            )
                        }
                    }
                }

                val response = client.get("/invalid")

                response.status shouldBe HttpStatusCode.UnprocessableEntity
                response.bodyAsText() shouldContain
                    """"errors":[{"field":"email","message":"must be a valid e-mail"},{"field":"password","message":"must have at least 8 characters"}]"""
            }
        }
    })
