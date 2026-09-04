package deadlines.platform.identity.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class EmailTest :
    StringSpec({
        "normalises case and surrounding spaces" {
            Email.of("  Tarik@Example.COM ")?.value shouldBe "tarik@example.com"
        }

        "accepts addresses with subdomains and plus tags" {
            Email.of("tarik+crm@mail.example.co.uk").shouldNotBeNull()
        }

        "rejects addresses without a domain part" {
            Email.of("tarik@example").shouldBeNull()
            Email.of("tarik").shouldBeNull()
        }

        "rejects addresses with spaces or a missing local part" {
            Email.of("tarik villalobos@example.com").shouldBeNull()
            Email.of("@example.com").shouldBeNull()
        }

        "rejects empty and oversized addresses" {
            Email.of("").shouldBeNull()
            Email.of("a".repeat(250) + "@example.com").shouldBeNull()
        }
    })
