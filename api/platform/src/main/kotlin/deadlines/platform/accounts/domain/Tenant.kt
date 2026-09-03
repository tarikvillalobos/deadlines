package deadlines.platform.accounts.domain

import kotlin.uuid.Uuid

enum class TenantStatus { ACTIVE, SUSPENDED }

data class Tenant(
    val id: Uuid,
    val name: String,
    val slug: Slug,
    val status: TenantStatus,
)
