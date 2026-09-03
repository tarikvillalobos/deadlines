package deadlines.app.database

import deadlines.platform.persistence.application.DatabaseSettings
import deadlines.platform.persistence.infrastructure.DatabaseFactory
import deadlines.platform.persistence.infrastructure.DatabaseHandle
import deadlines.platform.persistence.infrastructure.FlywayMigrator

/** Opens the connection pool, applies pending migrations and connects Exposed to the result. */
fun openDatabase(settings: DatabaseSettings = DatabaseSettings.fromEnvironment()): DatabaseHandle {
    val handle = DatabaseFactory.connect(settings)
    FlywayMigrator.migrate(handle.dataSource)
    return handle
}
