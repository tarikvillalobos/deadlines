package deadlines.app.support

import deadlines.platform.persistence.application.DatabaseHealth
import deadlines.platform.persistence.application.TransactionRunner
import org.koin.core.module.Module
import org.koin.dsl.module

class InMemoryTransactionRunner : TransactionRunner {
    override suspend fun <T> transaction(block: suspend () -> T): T = block()
}

fun fakeModules(databaseReachable: Boolean = true): List<Module> =
    listOf(
        module {
            single<TransactionRunner> { InMemoryTransactionRunner() }
            single<DatabaseHealth> { DatabaseHealth { databaseReachable } }
        },
    )
