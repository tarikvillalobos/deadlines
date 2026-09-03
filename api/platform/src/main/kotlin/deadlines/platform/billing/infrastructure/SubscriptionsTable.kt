package deadlines.platform.billing.infrastructure

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object SubscriptionsTable : Table("subscriptions") {
    val id = uuid("id")
    val tenantId = uuid("tenant_id")
    val status = text("status")
    val trialEndsAt = timestamp("trial_ends_at").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
