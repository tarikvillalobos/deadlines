package deadlines.app

import deadlines.app.database.openDatabase
import deadlines.app.di.persistenceModule
import deadlines.app.di.platformModule
import deadlines.app.plugins.configureApiVersioning
import deadlines.app.plugins.configureDependencyInjection
import deadlines.app.plugins.configureErrorHandling
import deadlines.app.plugins.configureSerialization
import deadlines.app.routes.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.module.Module

private const val DEFAULT_PORT = 8080

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
    val modules = listOf(persistenceModule(openDatabase()), platformModule)
    embeddedServer(Netty, port = port) { module(modules) }.start(wait = true)
}

fun Application.module(modules: List<Module>) {
    configureDependencyInjection(modules)
    configureSerialization()
    configureErrorHandling()
    configureApiVersioning()
    configureRouting()
}
