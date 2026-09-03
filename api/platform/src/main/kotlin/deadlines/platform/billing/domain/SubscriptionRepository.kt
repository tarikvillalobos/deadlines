package deadlines.platform.billing.domain

import java.util.UUID

interface SubscriptionRepository {
    suspend fun findByTenant(tenantId: UUID): Subscription?

    suspend fun create(subscription: Subscription): Subscription
}
