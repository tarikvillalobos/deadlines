package deadlines.app.database

import deadlines.app.support.IntegrationSpec
import deadlines.platform.persistence.infrastructure.ExposedTransactionRunner
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

private fun runSql(sql: String) = TransactionManager.current().connection.prepareStatement(sql, false).executeUpdate()

class TransactionRunnerTest :
    IntegrationSpec({
        val runner = ExposedTransactionRunner(database.database)

        "commits the work done inside the block" {
            runner.transaction { runSql("CREATE TABLE IF NOT EXISTS commit_probe (id int PRIMARY KEY)") }
            runner.transaction { runSql("INSERT INTO commit_probe (id) VALUES (1)") }

            queryRows("SELECT id FROM commit_probe") { it.getInt(1) } shouldContain 1
        }

        "rolls back everything when the block throws" {
            runner.transaction { runSql("CREATE TABLE IF NOT EXISTS rollback_probe (id int PRIMARY KEY)") }

            shouldThrow<IllegalStateException> {
                runner.transaction {
                    runSql("INSERT INTO rollback_probe (id) VALUES (99)")
                    error("failing on purpose")
                }
            }

            queryRows("SELECT id FROM rollback_probe") { it.getInt(1) } shouldNotContain 99
        }

        "returns the value produced by the block" {
            runner.transaction { 42 } shouldBe 42
        }
    })
