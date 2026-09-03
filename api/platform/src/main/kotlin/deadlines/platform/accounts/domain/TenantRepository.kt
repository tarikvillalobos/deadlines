package deadlines.platform.accounts.domain

import java.util.UUID

interface TenantRepository {
    suspend fun findById(id: UUID): Tenant?

    suspend fun existsBySlug(slug: Slug): Boolean

    suspend fun create(tenant: Tenant): Tenant
}
