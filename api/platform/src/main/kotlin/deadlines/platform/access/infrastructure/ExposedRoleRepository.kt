package deadlines.platform.access.infrastructure

import deadlines.platform.access.domain.GrantedPermission
import deadlines.platform.access.domain.Role
import deadlines.platform.access.domain.RoleRepository
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class ExposedRoleRepository : RoleRepository {
    override suspend fun findByKey(tenantId: Uuid, key: String) =
        RolesTable
            .selectAll()
            .where { (RolesTable.tenantId eq tenantId) and (RolesTable.key eq key) }
            .singleOrNull()
            ?.toRole()

    override suspend fun create(role: Role): Role {
        RolesTable.insert {
            it[id] = role.id
            it[tenantId] = role.tenantId
            it[key] = role.key
            it[name] = role.name
            it[isSystem] = role.isSystem
        }
        return role
    }

    override suspend fun grant(roleId: Uuid, permissions: List<GrantedPermission>) {
        RolePermissionsTable.batchInsert(permissions) { permission ->
            this[RolePermissionsTable.roleId] = roleId
            this[RolePermissionsTable.permissionKey] = permission.key
            this[RolePermissionsTable.scope] = permission.scope.name
        }
    }

    override suspend fun assign(tenantUserId: Uuid, roleId: Uuid) {
        UserRolesTable.insert {
            it[UserRolesTable.tenantUserId] = tenantUserId
            it[UserRolesTable.roleId] = roleId
        }
    }
}

private fun ResultRow.toRole() =
    Role(
        id = this[RolesTable.id],
        tenantId = this[RolesTable.tenantId],
        key = this[RolesTable.key],
        name = this[RolesTable.name],
        isSystem = this[RolesTable.isSystem],
    )
