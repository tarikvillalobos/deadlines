package deadlines.platform.identity.domain

import java.util.UUID

interface UserRepository {
    suspend fun findByEmail(email: Email): User?

    suspend fun findById(id: UUID): User?

    suspend fun create(user: User): User
}
