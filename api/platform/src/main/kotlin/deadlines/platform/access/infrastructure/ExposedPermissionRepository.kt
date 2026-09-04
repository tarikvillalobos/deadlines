package deadlines.platform.access.infrastructure

import deadlines.platform.access.domain.Permission
import deadlines.platform.access.domain.PermissionRepository
import org.jetbrains.exposed.v1.jdbc.selectAll

class ExposedPermissionRepository : PermissionRepository {
    override suspend fun all() =
        PermissionsTable
            .selectAll()
            .map {
                Permission(
                    key = it[PermissionsTable.key],
                    module = it[PermissionsTable.module],
                    description = it[PermissionsTable.description],
                )
            }
}
