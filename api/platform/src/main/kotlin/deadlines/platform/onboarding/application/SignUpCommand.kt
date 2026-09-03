package deadlines.platform.onboarding.application

import java.util.UUID

data class SignUpCommand(
    val companyName: String,
    val name: String,
    val email: String,
    val password: String,
)

data class SignUpResult(
    val tenantId: UUID,
    val tenantSlug: String,
    val userId: UUID,
    val email: String,
)
