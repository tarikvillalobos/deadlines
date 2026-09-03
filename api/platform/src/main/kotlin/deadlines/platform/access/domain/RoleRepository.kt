package deadlines.platform.access.domain

import java.util.UUID

interface RoleRepository {
    suspend fun findByKey(tenantId: UUID, key: String): Role?

    suspend fun create(role: Role): Role

    suspend fun grant(roleId: UUID, permissions: List<GrantedPermission>)

    suspend fun assign(tenantUserId: UUID, roleId: UUID)
}
