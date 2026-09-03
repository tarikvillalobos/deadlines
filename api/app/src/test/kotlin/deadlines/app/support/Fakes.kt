package deadlines.app.support

import deadlines.app.AppDependencies
import deadlines.platform.persistence.application.DatabaseHealth
import deadlines.platform.persistence.application.TransactionRunner

class InMemoryTransactionRunner : TransactionRunner {
    override suspend fun <T> transaction(block: suspend () -> T): T = block()
}

fun fakeDependencies(databaseReachable: Boolean = true) =
    AppDependencies(
        transactionRunner = InMemoryTransactionRunner(),
        databaseHealth = DatabaseHealth { databaseReachable },
    )
