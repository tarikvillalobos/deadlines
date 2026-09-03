package deadlines.app.routes

import deadlines.app.module
import deadlines.app.support.fakeDependencies
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication

class HealthRoutesTest :
    StringSpec({
        "GET /api/health reports the service is up when the database answers" {
            testApplication {
                application { module(fakeDependencies(databaseReachable = true)) }

                val response = client.get("/api/health")

                response.status shouldBe HttpStatusCode.OK
                response.contentType()?.withoutParameters() shouldBe ContentType.Application.Json
                response.bodyAsText() shouldBe """{"status":"ok","database":"ok"}"""
            }
        }

        "GET /api/health reports degraded when the database is unreachable" {
            testApplication {
                application { module(fakeDependencies(databaseReachable = false)) }

                val response = client.get("/api/health")

                response.status shouldBe HttpStatusCode.ServiceUnavailable
                response.bodyAsText() shouldBe """{"status":"degraded","database":"unavailable"}"""
            }
        }
    })
