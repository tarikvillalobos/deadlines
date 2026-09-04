package deadlines.platform.identity.domain

import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun findByEmail(email: Email): User?

    suspend fun findById(id: Uuid): User?

    suspend fun create(user: User): User
}
