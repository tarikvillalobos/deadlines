package deadlines.platform.persistence.infrastructure

import org.flywaydb.core.Flyway
import javax.sql.DataSource

object FlywayMigrator {
    fun migrate(dataSource: DataSource) {
        Flyway
            .configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            .migrate()
    }
}
