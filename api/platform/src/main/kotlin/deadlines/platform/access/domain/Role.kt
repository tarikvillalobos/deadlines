package deadlines.platform.access.domain

import java.util.UUID

data class Role(
    val id: UUID,
    val tenantId: UUID,
    val key: String,
    val name: String,
    val isSystem: Boolean,
)

enum class SystemRole(val key: String, val displayName: String) {
    OWNER("owner", "Owner"),
    ADMIN("admin", "Admin"),
    MEMBER("member", "Member"),
}
