package deadlines.app.plugins

import deadlines.contracts.version.ApiVersion
import deadlines.contracts.version.ApiVersions
import deadlines.core.error.AppError
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.request.header
import io.ktor.server.response.header
import io.ktor.util.AttributeKey

private val ApiVersionKey = AttributeKey<ApiVersion>("ApiVersion")

/** Resolves the requested API version from the [ApiVersions.HEADER], defaulting to the latest, and echoes it back. */
val ApiVersioning =
    createApplicationPlugin("ApiVersioning") {
        onCall { call ->
            val raw = call.request.header(ApiVersions.HEADER)
            val version = ApiVersions.parse(raw) ?: throw AppError.BadRequest(unknownVersionMessage(raw))
            call.attributes.put(ApiVersionKey, version)
            call.response.header(ApiVersions.HEADER, version.toString())
        }
    }

val ApplicationCall.apiVersion: ApiVersion
    get() = attributes[ApiVersionKey]

fun Application.configureApiVersioning() {
    install(ApiVersioning)
}

private fun unknownVersionMessage(raw: String?) =
    "Unknown API version '$raw'. Supported versions: ${ApiVersions.all.joinToString(", ")}"
