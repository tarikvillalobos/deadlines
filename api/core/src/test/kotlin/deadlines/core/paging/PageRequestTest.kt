package deadlines.core.paging

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PageRequestTest :
    StringSpec({
        "computes the offset from page and size" {
            PageRequest(page = 3, size = 20).offset shouldBe 40L
        }

        "rejects pages below 1" {
            shouldThrow<IllegalArgumentException> { PageRequest(page = 0) }
        }

        "rejects sizes above the maximum" {
            shouldThrow<IllegalArgumentException> { PageRequest(size = PageRequest.MAX_SIZE + 1) }
        }

        "page computes total pages" {
            Page(items = emptyList<Int>(), page = 1, size = 20, total = 41).totalPages shouldBe 3
            Page(items = emptyList<Int>(), page = 1, size = 20, total = 0).totalPages shouldBe 0
        }
    })
