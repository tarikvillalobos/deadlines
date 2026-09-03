package deadlines.platform.persistence.infrastructure

import deadlines.platform.persistence.application.TransactionRunner
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

class ExposedTransactionRunner(private val database: Database) : TransactionRunner {
    override suspend fun <T> transaction(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
