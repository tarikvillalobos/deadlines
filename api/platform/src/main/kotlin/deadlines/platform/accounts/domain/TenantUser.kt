package deadlines.platform.accounts.domain

import java.util.UUID

enum class TenantUserStatus { ACTIVE, INACTIVE }

data class TenantUser(
    val id: UUID,
    val tenantId: UUID,
    val userId: UUID,
    val status: TenantUserStatus,
)
