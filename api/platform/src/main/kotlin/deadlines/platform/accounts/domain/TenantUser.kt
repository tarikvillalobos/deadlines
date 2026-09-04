package deadlines.platform.accounts.domain

import kotlin.uuid.Uuid

enum class TenantUserStatus { ACTIVE, INACTIVE }

data class TenantUser(
    val id: Uuid,
    val tenantId: Uuid,
    val userId: Uuid,
    val status: TenantUserStatus,
)
