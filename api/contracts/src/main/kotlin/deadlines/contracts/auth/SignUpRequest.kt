package deadlines.contracts.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val companyName: String,
    val name: String,
    val email: String,
    val password: String,
)
