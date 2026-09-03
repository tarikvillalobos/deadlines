package deadlines.platform.persistence.infrastructure

import deadlines.platform.persistence.application.DatabaseHealth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import javax.sql.DataSource

private const val VALIDATION_TIMEOUT_SECONDS = 2

class JdbcDatabaseHealth(private val dataSource: DataSource) : DatabaseHealth {
    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun isReachable() =
        withContext(Dispatchers.IO) {
            runCatching {
                dataSource.connection.use { it.isValid(VALIDATION_TIMEOUT_SECONDS) }
            }.onFailure { logger.warn("Database health check failed", it) }
                .getOrDefault(false)
        }
}
