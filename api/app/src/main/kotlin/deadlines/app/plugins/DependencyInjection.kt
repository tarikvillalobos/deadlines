package deadlines.app.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.module.Module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection(modules: List<Module>) {
    install(Koin) {
        slf4jLogger()
        modules(modules)
    }
}
