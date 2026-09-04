package deadlines.app.di

import deadlines.platform.persistence.application.DatabaseHealth
import deadlines.platform.persistence.application.TransactionRunner
import deadlines.platform.persistence.infrastructure.DatabaseHandle
import deadlines.platform.persistence.infrastructure.ExposedTransactionRunner
import deadlines.platform.persistence.infrastructure.JdbcDatabaseHealth
import org.koin.dsl.module

fun persistenceModule(handle: DatabaseHandle) =
    module {
        single { handle }
        single<TransactionRunner> { ExposedTransactionRunner(handle.database) }
        single<DatabaseHealth> { JdbcDatabaseHealth(handle.dataSource) }
    }
