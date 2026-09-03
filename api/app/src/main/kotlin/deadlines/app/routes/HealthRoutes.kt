package deadlines.app.routes

import deadlines.contracts.health.HealthResponse
import deadlines.platform.persistence.application.DatabaseHealth
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes(databaseHealth: DatabaseHealth) {
    get("/health") {
        val databaseReachable = databaseHealth.isReachable()
        val body =
            HealthResponse(
                status = if (databaseReachable) "ok" else "degraded",
                database = if (databaseReachable) "ok" else "unavailable",
            )
        call.respond(if (databaseReachable) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable, body)
    }
}
