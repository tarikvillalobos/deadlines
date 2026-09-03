package deadlines.core.id

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.shouldBe

class IdsTest :
    StringSpec({
        "generates version 7 identifiers" {
            Ids.next().version() shouldBe 7
        }

        "generates unique identifiers" {
            val ids = List(1_000) { Ids.next() }

            ids.toSet().size shouldBe ids.size
        }

        "generates identifiers that sort by creation time" {
            val ids = List(100) { Ids.next().toString() }

            ids.shouldBeSorted()
        }
    })
