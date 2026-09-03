package deadlines.platform.access.domain

enum class Scope { OWN, TEAM, ALL }

data class Permission(
    val key: String,
    val module: String,
    val description: String,
)

data class GrantedPermission(val key: String, val scope: Scope)
