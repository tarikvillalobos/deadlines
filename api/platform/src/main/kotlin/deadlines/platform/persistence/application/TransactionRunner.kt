package deadlines.platform.persistence.application

/** Runs a block inside a single database transaction, rolling back if it throws. */
interface TransactionRunner {
    suspend fun <T> transaction(block: suspend () -> T): T
}
