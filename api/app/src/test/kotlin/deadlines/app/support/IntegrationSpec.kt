package deadlines.app.support

import deadlines.app.AppDependencies
import deadlines.app.database.openDatabase
import deadlines.platform.persistence.application.DatabaseSettings
import deadlines.platform.persistence.infrastructure.DatabaseHandle
import io.kotest.core.spec.style.StringSpec
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

private val container: PostgreSQLContainer<*> by lazy {
    PostgreSQLContainer("postgres:16-alpine").apply {
        withReuse(true)
        start()
    }
}

private val handle: DatabaseHandle by lazy {
    openDatabase(
        DatabaseSettings(
            url = container.jdbcUrl,
            user = container.username,
            password = container.password,
        ),
    )
}

/** Base class for tests that need a real PostgreSQL instance with every migration applied. */
abstract class IntegrationSpec(body: IntegrationSpec.() -> Unit) : StringSpec() {
    val database get() = handle
    val dependencies: AppDependencies get() = AppDependencies.from(handle)

    fun <T> queryRows(sql: String, readRow: (ResultSet) -> T): List<T> =
        handle.dataSource.connection.use { connection ->
            connection.createStatement().executeQuery(sql).use { rows ->
                buildList { while (rows.next()) add(readRow(rows)) }
            }
        }

    init {
        body()
    }
}
