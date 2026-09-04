package deadlines.platform.identity.infrastructure

import deadlines.platform.identity.domain.Email
import deadlines.platform.identity.domain.PasswordHash
import deadlines.platform.identity.domain.User
import deadlines.platform.identity.domain.UserRepository
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class ExposedUserRepository : UserRepository {
    override suspend fun findByEmail(email: Email) =
        UsersTable
            .selectAll()
            .where { UsersTable.email eq email.value }
            .singleOrNull()
            ?.toUser()

    override suspend fun findById(id: Uuid) =
        UsersTable
            .selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toUser()

    override suspend fun create(user: User): User {
        UsersTable.insert {
            it[id] = user.id
            it[email] = user.email.value
            it[passwordHash] = user.passwordHash.value
            it[name] = user.name
        }
        return user
    }
}

private fun ResultRow.toUser() =
    User(
        id = this[UsersTable.id],
        email = requireNotNull(Email.of(this[UsersTable.email])),
        passwordHash = PasswordHash(this[UsersTable.passwordHash]),
        name = this[UsersTable.name],
    )
