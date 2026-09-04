package deadlines.platform.accounts.infrastructure

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object TenantsTable : Table("tenants") {
    val id = uuid("id")
    val name = text("name")
    val slug = text("slug")
    val status = text("status")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}

object TenantUsersTable : Table("tenant_users") {
    val id = uuid("id")
    val tenantId = uuid("tenant_id").references(TenantsTable.id)
    val userId = uuid("user_id")
    val status = text("status")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
