package deadlines.platform.billing.infrastructure

import deadlines.platform.billing.domain.Subscription
import deadlines.platform.billing.domain.SubscriptionRepository
import deadlines.platform.billing.domain.SubscriptionStatus
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class ExposedSubscriptionRepository : SubscriptionRepository {
    override suspend fun findByTenant(tenantId: Uuid) =
        SubscriptionsTable
            .selectAll()
            .where { SubscriptionsTable.tenantId eq tenantId }
            .singleOrNull()
            ?.toSubscription()

    override suspend fun create(subscription: Subscription): Subscription {
        SubscriptionsTable.insert {
            it[id] = subscription.id
            it[tenantId] = subscription.tenantId
            it[status] = subscription.status.name
            it[trialEndsAt] = subscription.trialEndsAt
        }
        return subscription
    }
}

private fun ResultRow.toSubscription() =
    Subscription(
        id = this[SubscriptionsTable.id],
        tenantId = this[SubscriptionsTable.tenantId],
        status = SubscriptionStatus.valueOf(this[SubscriptionsTable.status]),
        trialEndsAt = this[SubscriptionsTable.trialEndsAt],
    )
