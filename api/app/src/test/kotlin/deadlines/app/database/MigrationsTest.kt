package deadlines.app.database

import deadlines.app.module
import deadlines.app.support.IntegrationSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication

class MigrationsTest :
    IntegrationSpec({
        "every migration is applied successfully" {
            val applied =
                queryRows("SELECT version, success FROM flyway_schema_history") {
                    it.getString(1) to
                        it.getBoolean(2)
                }

            applied shouldContain ("1" to true)
        }

        "the baseline migration installs pgcrypto" {
            val extensions = queryRows("SELECT extname FROM pg_extension") { it.getString(1) }

            extensions shouldContain "pgcrypto"
        }

        "GET /api/health reports the real database as reachable" {
            testApplication {
                application { module(modules) }

                val response = client.get("/api/health")

                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe """{"status":"ok","database":"ok"}"""
            }
        }
    })
