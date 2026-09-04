package deadlines.platform.identity.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class RawPasswordTest :
    StringSpec({
        "accepts any password long enough" {
            RawPassword.of("correct horse battery staple").shouldNotBeNull()
            RawPassword.of("12345678").shouldNotBeNull()
        }

        "rejects passwords below the minimum length" {
            RawPassword.of("1234567").shouldBeNull()
            RawPassword.of("").shouldBeNull()
        }

        "rejects passwords above the maximum length" {
            RawPassword.of("a".repeat(RawPassword.MAXIMUM_LENGTH + 1)).shouldBeNull()
        }

        "never reveals the password when printed" {
            RawPassword.of("super secret value").toString() shouldBe "RawPassword(hidden)"
        }
    })
