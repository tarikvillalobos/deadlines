package deadlines.app.routes

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        route("/api") {
            healthRoutes()
        }
    }
}
