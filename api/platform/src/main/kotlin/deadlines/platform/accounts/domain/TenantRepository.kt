package deadlines.platform.accounts.domain

import kotlin.uuid.Uuid

interface TenantRepository {
    suspend fun findById(id: Uuid): Tenant?

    suspend fun existsBySlug(slug: Slug): Boolean

    suspend fun create(tenant: Tenant): Tenant
}
