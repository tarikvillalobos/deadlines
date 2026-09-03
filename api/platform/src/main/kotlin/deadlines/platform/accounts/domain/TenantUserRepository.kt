package deadlines.platform.accounts.domain

import java.util.UUID

interface TenantUserRepository {
    suspend fun find(tenantId: UUID, userId: UUID): TenantUser?

    suspend fun create(tenantUser: TenantUser): TenantUser
}
