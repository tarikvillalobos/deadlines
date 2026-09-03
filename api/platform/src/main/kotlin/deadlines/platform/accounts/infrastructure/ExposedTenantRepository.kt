package deadlines.platform.accounts.infrastructure

import deadlines.platform.accounts.domain.Slug
import deadlines.platform.accounts.domain.Tenant
import deadlines.platform.accounts.domain.TenantRepository
import deadlines.platform.accounts.domain.TenantStatus
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class ExposedTenantRepository : TenantRepository {
    override suspend fun findById(id: Uuid) =
        TenantsTable
            .selectAll()
            .where { TenantsTable.id eq id }
            .singleOrNull()
            ?.toTenant()

    override suspend fun existsBySlug(slug: Slug) =
        TenantsTable
            .selectAll()
            .where { TenantsTable.slug eq slug.value }
            .empty()
            .not()

    override suspend fun create(tenant: Tenant): Tenant {
        TenantsTable.insert {
            it[id] = tenant.id
            it[name] = tenant.name
            it[slug] = tenant.slug.value
            it[status] = tenant.status.name
        }
        return tenant
    }
}

private fun ResultRow.toTenant() =
    Tenant(
        id = this[TenantsTable.id],
        name = this[TenantsTable.name],
        slug = Slug.of(this[TenantsTable.slug]),
        status = TenantStatus.valueOf(this[TenantsTable.status]),
    )
