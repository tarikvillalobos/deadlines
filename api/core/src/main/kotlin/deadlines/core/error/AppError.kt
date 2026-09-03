package deadlines.core.error

sealed class AppError(
    val status: Int,
    val type: String,
    val title: String,
    val detail: String?,
) : RuntimeException(detail ?: title) {
    class BadRequest(detail: String) : AppError(400, "bad-request", "Bad request", detail)

    class Unauthorized(detail: String? = null) : AppError(401, "unauthorized", "Authentication required", detail)

    class Forbidden(detail: String? = null) : AppError(403, "forbidden", "Permission denied", detail)

    class NotFound(detail: String) : AppError(404, "not-found", "Resource not found", detail)

    class Conflict(detail: String) : AppError(409, "conflict", "Conflict", detail)

    class Validation(val violations: List<Violation>) : AppError(422, "validation", "Validation failed", null)

    data class Violation(val field: String, val message: String)
}
