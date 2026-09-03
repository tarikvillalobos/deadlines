package deadlines.app.routes

import deadlines.contracts.auth.SignUpRequest
import deadlines.contracts.auth.SignUpResponse
import deadlines.core.error.AppError
import deadlines.platform.onboarding.application.SignUp
import deadlines.platform.onboarding.application.SignUpCommand
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val signUp by inject<SignUp>()

    route("/auth") {
        post("/signup") {
            val request =
                runCatching { call.receive<SignUpRequest>() }
                    .getOrElse { throw AppError.BadRequest("The request body is not a valid sign-up payload") }

            val result =
                signUp(
                    SignUpCommand(
                        companyName = request.companyName,
                        name = request.name,
                        email = request.email,
                        password = request.password,
                    ),
                )

            call.respond(
                HttpStatusCode.Created,
                SignUpResponse(
                    tenantId = result.tenantId.toString(),
                    tenantSlug = result.tenantSlug,
                    userId = result.userId.toString(),
                    email = result.email,
                ),
            )
        }
    }
}
