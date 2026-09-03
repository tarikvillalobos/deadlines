package deadlines.platform.billing.domain

import kotlin.uuid.Uuid

interface SubscriptionRepository {
    suspend fun findByTenant(tenantId: Uuid): Subscription?

    suspend fun create(subscription: Subscription): Subscription
}
