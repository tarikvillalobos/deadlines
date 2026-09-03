package deadlines.contracts.problem

import kotlinx.serialization.Serializable

/** Error body following RFC 9457 (Problem Details for HTTP APIs). */
@Serializable
data class Problem(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String? = null,
    val errors: List<FieldViolation> = emptyList(),
) {
    companion object {
        const val TYPE_PREFIX = "urn:deadlines:problem:"
    }
}

@Serializable
data class FieldViolation(val field: String, val message: String)
