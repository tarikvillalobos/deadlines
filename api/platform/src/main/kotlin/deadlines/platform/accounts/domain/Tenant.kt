package deadlines.platform.accounts.domain

import java.util.UUID

enum class TenantStatus { ACTIVE, SUSPENDED }

data class Tenant(
    val id: UUID,
    val name: String,
    val slug: Slug,
    val status: TenantStatus,
)
