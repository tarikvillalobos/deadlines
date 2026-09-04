package deadlines.platform.accounts.domain

import kotlin.uuid.Uuid

interface TenantUserRepository {
    suspend fun find(tenantId: Uuid, userId: Uuid): TenantUser?

    suspend fun create(tenantUser: TenantUser): TenantUser
}
