package deadlines.app

import deadlines.app.database.openDatabase
import deadlines.platform.persistence.application.DatabaseHealth
import deadlines.platform.persistence.application.TransactionRunner
import deadlines.platform.persistence.infrastructure.DatabaseHandle
import deadlines.platform.persistence.infrastructure.ExposedTransactionRunner
import deadlines.platform.persistence.infrastructure.JdbcDatabaseHealth

class AppDependencies(val transactionRunner: TransactionRunner, val databaseHealth: DatabaseHealth) {
    companion object {
        fun from(handle: DatabaseHandle) =
            AppDependencies(
                transactionRunner = ExposedTransactionRunner(handle.database),
                databaseHealth = JdbcDatabaseHealth(handle.dataSource),
            )

        fun production() = from(openDatabase())
    }
}
