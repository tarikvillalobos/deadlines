package deadlines.platform.access.infrastructure

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object PermissionsTable : Table("permissions") {
    val key = text("key")
    val module = text("module")
    val description = text("description")

    override val primaryKey = PrimaryKey(key)
}

object RolesTable : Table("roles") {
    val id = uuid("id")
    val tenantId = uuid("tenant_id")
    val key = text("key")
    val name = text("name")
    val isSystem = bool("is_system")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}

object RolePermissionsTable : Table("role_permissions") {
    val roleId = uuid("role_id").references(RolesTable.id)
    val permissionKey = text("permission_key").references(PermissionsTable.key)
    val scope = text("scope")

    override val primaryKey = PrimaryKey(roleId, permissionKey)
}

object UserRolesTable : Table("user_roles") {
    val tenantUserId = uuid("tenant_user_id")
    val roleId = uuid("role_id").references(RolesTable.id)

    override val primaryKey = PrimaryKey(tenantUserId, roleId)
}
