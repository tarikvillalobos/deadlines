package deadlines.platform.persistence.infrastructure

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import deadlines.platform.persistence.application.DatabaseSettings
import org.jetbrains.exposed.v1.jdbc.Database
import javax.sql.DataSource

data class DatabaseHandle(val dataSource: DataSource, val database: Database)

object DatabaseFactory {
    fun connect(settings: DatabaseSettings): DatabaseHandle {
        val dataSource = dataSource(settings)
        return DatabaseHandle(dataSource, Database.connect(dataSource))
    }

    fun dataSource(settings: DatabaseSettings): DataSource =
        HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = settings.url
                username = settings.user
                password = settings.password
                maximumPoolSize = settings.maxPoolSize
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                poolName = "deadlines"
            },
        )
}
