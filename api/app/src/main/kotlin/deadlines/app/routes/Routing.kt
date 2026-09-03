package deadlines.app.routes

import deadlines.app.AppDependencies
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting(dependencies: AppDependencies) {
    routing {
        route("/api") {
            healthRoutes(dependencies.databaseHealth)
        }
    }
}
