package deadlines.app.routes

import deadlines.contracts.health.HealthResponse
import deadlines.platform.persistence.application.DatabaseHealth
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.healthRoutes() {
    val databaseHealth by inject<DatabaseHealth>()

    get("/health") {
        val reachable = databaseHealth.isReachable()
        val body =
            HealthResponse(
                status = if (reachable) "ok" else "degraded",
                database = if (reachable) "ok" else "unavailable",
            )
        call.respond(if (reachable) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable, body)
    }
}
