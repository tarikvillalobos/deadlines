package deadlines.app

import deadlines.app.plugins.configureApiVersioning
import deadlines.app.plugins.configureErrorHandling
import deadlines.app.plugins.configureSerialization
import deadlines.app.routes.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

private const val DEFAULT_PORT = 8080

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureErrorHandling()
    configureApiVersioning()
    configureRouting()
}
