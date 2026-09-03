package deadlines.app.routes

import deadlines.app.module
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
        "GET /api/health reports the service is up" {
            testApplication {
                application { module() }

                val response = client.get("/api/health")

                response.status shouldBe HttpStatusCode.OK
                response.contentType()?.withoutParameters() shouldBe ContentType.Application.Json
                response.bodyAsText() shouldBe """{"status":"ok"}"""
            }
        }
    })
