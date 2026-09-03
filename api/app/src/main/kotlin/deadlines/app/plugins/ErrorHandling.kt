package deadlines.app.plugins

import deadlines.contracts.problem.FieldViolation
import deadlines.contracts.problem.Problem
import deadlines.core.error.AppError
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<AppError> { call, error -> call.respondProblem(error.toProblem()) }
    }
}

suspend fun ApplicationCall.respondProblem(problem: Problem) {
    respondText(
        text = ApiJson.encodeToString(problem),
        contentType = ContentType.Application.ProblemJson,
        status = HttpStatusCode.fromValue(problem.status),
    )
}

fun AppError.toProblem() =
    Problem(
        type = Problem.TYPE_PREFIX + type,
        title = title,
        status = status,
        detail = detail,
        errors = violations().map { FieldViolation(it.field, it.message) },
    )

private fun AppError.violations() = if (this is AppError.Validation) violations else emptyList()
