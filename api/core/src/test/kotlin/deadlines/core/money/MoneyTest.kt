package deadlines.core.money

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MoneyTest :
    StringSpec({
        "adds, subtracts and multiplies by quantity" {
            Money(1_050) + Money(250) shouldBe Money(1_300)
            Money(1_050) - Money(250) shouldBe Money(800)
            Money(1_050) * 3 shouldBe Money(3_150)
            -Money(1_050) shouldBe Money(-1_050)
        }

        "compares by amount" {
            (Money(100) > Money(99)) shouldBe true
            (Money(100) < Money(101)) shouldBe true
            Money(100).compareTo(Money(100)) shouldBe 0
        }

        "applies a percentage in basis points" {
            Money(10_000).percent(1_550) shouldBe Money(1_550)
            Money(19_990).percent(1_000) shouldBe Money(1_999)
        }

        "rounds percentages half to even" {
            Money(101).percent(5_000) shouldBe Money(50)
            Money(103).percent(5_000) shouldBe Money(52)
        }

        "allocates without losing cents" {
            Money(100).allocate(3) shouldBe listOf(Money(34), Money(33), Money(33))
            Money(100).allocate(3).sumOf { it.cents } shouldBe 100L
        }

        "allocates exact divisions evenly" {
            Money(300).allocate(3) shouldBe listOf(Money(100), Money(100), Money(100))
        }

        "allocates negative amounts symmetrically" {
            Money(-100).allocate(3) shouldBe listOf(Money(-34), Money(-33), Money(-33))
        }

        "rejects allocation into zero parts" {
            shouldThrow<IllegalArgumentException> { Money(100).allocate(0) }
        }
    })
