package deadlines.platform.access.domain

import kotlin.uuid.Uuid

interface RoleRepository {
    suspend fun findByKey(tenantId: Uuid, key: String): Role?

    suspend fun create(role: Role): Role

    suspend fun grant(roleId: Uuid, permissions: List<GrantedPermission>)

    suspend fun assign(tenantUserId: Uuid, roleId: Uuid)
}
