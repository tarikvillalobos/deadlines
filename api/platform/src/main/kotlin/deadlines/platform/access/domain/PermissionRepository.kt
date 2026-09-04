package deadlines.platform.access.domain

interface PermissionRepository {
    suspend fun all(): List<Permission>
}
