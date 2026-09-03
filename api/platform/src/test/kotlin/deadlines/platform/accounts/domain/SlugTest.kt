package deadlines.platform.accounts.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SlugTest :
    StringSpec({
        "lowercases and joins words with hyphens" {
            Slug.from("Acme Industries").value shouldBe "acme-industries"
        }

        "strips accents" {
            Slug.from("Confecções São Paulo").value shouldBe "confeccoes-sao-paulo"
        }

        "collapses symbols and trims the edges" {
            Slug.from("  ***Acme & Co.!!  ").value shouldBe "acme-co"
        }

        "falls back when the name has no usable characters" {
            Slug.from("***").value shouldBe Slug.FALLBACK
            Slug.from("   ").value shouldBe Slug.FALLBACK
        }

        "keeps the slug within the maximum length" {
            Slug.from("a".repeat(100)).value.length shouldBe 48
        }

        "appends a numeric suffix without exceeding the maximum length" {
            val suffixed = Slug.from("a".repeat(100)).withSuffix(2)

            suffixed.value.length shouldBe 48
            suffixed.value.endsWith("-2") shouldBe true
        }
    })
