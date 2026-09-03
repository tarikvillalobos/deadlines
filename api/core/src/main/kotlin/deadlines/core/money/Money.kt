package deadlines.core.money

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

@JvmInline
value class Money(val cents: Long) : Comparable<Money> {
    operator fun plus(other: Money) = Money(cents + other.cents)

    operator fun minus(other: Money) = Money(cents - other.cents)

    operator fun times(quantity: Int) = Money(cents * quantity)

    operator fun unaryMinus() = Money(-cents)

    override fun compareTo(other: Money) = cents.compareTo(other.cents)

    /** Applies a percentage expressed in basis points (1% = 100 bp), rounding half to even. */
    fun percent(basisPoints: Int): Money {
        val result =
            BigDecimal
                .valueOf(cents)
                .multiply(BigDecimal.valueOf(basisPoints.toLong()))
                .divide(BASIS_POINTS_PER_UNIT, 0, RoundingMode.HALF_EVEN)
        return Money(result.longValueExact())
    }

    /** Splits the amount into equal shares that add up exactly to the original, giving leftover cents to the first shares. */
    fun allocate(parts: Int): List<Money> {
        require(parts > 0) { "parts must be at least 1" }
        val base = cents / parts
        val remainder = cents - base * parts
        val step = if (remainder >= 0) 1L else -1L
        val sharesWithExtraCent = abs(remainder)
        return List(parts) { index -> Money(base + if (index < sharesWithExtraCent) step else 0) }
    }

    override fun toString() = "Money($cents)"

    companion object {
        val ZERO = Money(0)
        private val BASIS_POINTS_PER_UNIT = BigDecimal.valueOf(10_000)
    }
}
