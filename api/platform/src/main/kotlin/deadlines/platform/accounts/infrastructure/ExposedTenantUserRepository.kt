package deadlines.platform.accounts.infrastructure

import deadlines.platform.accounts.domain.TenantUser
import deadlines.platform.accounts.domain.TenantUserRepository
import deadlines.platform.accounts.domain.TenantUserStatus
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class ExposedTenantUserRepository : TenantUserRepository {
    override suspend fun find(tenantId: Uuid, userId: Uuid) =
        TenantUsersTable
            .selectAll()
            .where { (TenantUsersTable.tenantId eq tenantId) and (TenantUsersTable.userId eq userId) }
            .singleOrNull()
            ?.toTenantUser()

    override suspend fun create(tenantUser: TenantUser): TenantUser {
        TenantUsersTable.insert {
            it[id] = tenantUser.id
            it[tenantId] = tenantUser.tenantId
            it[userId] = tenantUser.userId
            it[status] = tenantUser.status.name
        }
        return tenantUser
    }
}

private fun ResultRow.toTenantUser() =
    TenantUser(
        id = this[TenantUsersTable.id],
        tenantId = this[TenantUsersTable.tenantId],
        userId = this[TenantUsersTable.userId],
        status = TenantUserStatus.valueOf(this[TenantUsersTable.status]),
    )
