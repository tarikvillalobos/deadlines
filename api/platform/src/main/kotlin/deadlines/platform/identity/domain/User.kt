package deadlines.platform.identity.domain

import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val email: Email,
    val passwordHash: PasswordHash,
    val name: String,
)
