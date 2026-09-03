package deadlines.platform.access.domain

import kotlin.uuid.Uuid

data class Role(
    val id: Uuid,
    val tenantId: Uuid,
    val key: String,
    val name: String,
    val isSystem: Boolean,
)

enum class SystemRole(val key: String, val displayName: String) {
    OWNER("owner", "Owner"),
    ADMIN("admin", "Admin"),
    MEMBER("member", "Member"),
}
