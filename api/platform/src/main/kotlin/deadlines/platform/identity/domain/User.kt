package deadlines.platform.identity.domain

import java.util.UUID

data class User(
    val id: UUID,
    val email: Email,
    val passwordHash: PasswordHash,
    val name: String,
)
