package deadlines.platform.onboarding.application

import kotlin.uuid.Uuid

data class SignUpCommand(
    val companyName: String,
    val name: String,
    val email: String,
    val password: String,
)

data class SignUpResult(
    val tenantId: Uuid,
    val tenantSlug: String,
    val userId: Uuid,
    val email: String,
)
