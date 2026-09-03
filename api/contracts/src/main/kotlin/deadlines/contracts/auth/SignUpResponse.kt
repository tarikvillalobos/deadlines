package deadlines.contracts.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val tenantId: String,
    val tenantSlug: String,
    val userId: String,
    val email: String,
)
