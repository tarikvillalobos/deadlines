package deadlines.platform.billing.domain

import kotlin.time.Instant
import kotlin.uuid.Uuid

enum class SubscriptionStatus { TRIALING, ACTIVE, PAST_DUE, CANCELED, EXPIRED }

data class Subscription(
    val id: Uuid,
    val tenantId: Uuid,
    val status: SubscriptionStatus,
    val trialEndsAt: Instant?,
)
