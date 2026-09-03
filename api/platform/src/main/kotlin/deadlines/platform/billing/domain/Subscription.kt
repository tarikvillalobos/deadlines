package deadlines.platform.billing.domain

import kotlinx.datetime.Instant
import java.util.UUID

enum class SubscriptionStatus { TRIALING, ACTIVE, PAST_DUE, CANCELED, EXPIRED }

data class Subscription(
    val id: UUID,
    val tenantId: UUID,
    val status: SubscriptionStatus,
    val trialEndsAt: Instant?,
)
