package deadlines.app.database

import deadlines.app.support.IntegrationSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

class TransactionRunnerTest :
    IntegrationSpec({
        val runner = dependencies.transactionRunner

        "commits the work done inside the block" {
            runner.transaction { execute("CREATE TABLE IF NOT EXISTS commit_probe (id int PRIMARY KEY)") }
            runner.transaction { execute("INSERT INTO commit_probe (id) VALUES (1)") }

            queryRows("SELECT id FROM commit_probe") { it.getInt(1) } shouldContain 1
        }

        "rolls back everything when the block throws" {
            runner.transaction { execute("CREATE TABLE IF NOT EXISTS rollback_probe (id int PRIMARY KEY)") }

            shouldThrow<IllegalStateException> {
                runner.transaction {
                    execute("INSERT INTO rollback_probe (id) VALUES (99)")
                    error("failing on purpose")
                }
            }

            queryRows("SELECT id FROM rollback_probe") { it.getInt(1) } shouldNotContain 99
        }

        "returns the value produced by the block" {
            runner.transaction { 42 } shouldBe 42
        }
    })

private fun execute(sql: String) =
    TransactionManager
        .current()
        .connection
        .prepareStatement(sql, false)
        .executeUpdate()
