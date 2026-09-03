package deadlines.platform.identity.infrastructure

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object UsersTable : Table("users") {
    val id = uuid("id")
    val email = text("email")
    val passwordHash = text("password_hash")
    val name = text("name")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
