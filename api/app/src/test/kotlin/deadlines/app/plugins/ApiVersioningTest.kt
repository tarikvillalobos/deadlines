package deadlines.app.plugins

import deadlines.app.module
import deadlines.app.support.fakeModules
import deadlines.contracts.version.ApiVersions
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication

class ApiVersioningTest :
    StringSpec({
        "requests without the header use the latest version" {
            testApplication {
                application { module(fakeModules()) }

                val response = client.get("/api/health")

                response.status shouldBe HttpStatusCode.OK
                response.headers[ApiVersions.HEADER] shouldBe ApiVersions.latest.toString()
            }
        }

        "requests pinning a known version get it echoed back" {
            testApplication {
                application { module(fakeModules()) }

                val response = client.get("/api/health") { header(ApiVersions.HEADER, "1") }

                response.status shouldBe HttpStatusCode.OK
                response.headers[ApiVersions.HEADER] shouldBe "1"
            }
        }

        "requests pinning an unknown version are rejected" {
            testApplication {
                application { module(fakeModules()) }

                val response = client.get("/api/health") { header(ApiVersions.HEADER, "99") }

                response.status shouldBe HttpStatusCode.BadRequest
                response.contentType()?.withoutParameters() shouldBe ContentType.Application.ProblemJson
                response.bodyAsText() shouldContain """"type":"urn:deadlines:problem:bad-request""""
                response.bodyAsText() shouldContain "Unknown API version '99'. Supported versions: 1"
            }
        }
    })
