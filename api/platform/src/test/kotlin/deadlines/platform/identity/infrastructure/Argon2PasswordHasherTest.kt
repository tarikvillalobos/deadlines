package deadlines.platform.identity.infrastructure

import deadlines.platform.identity.domain.PasswordHash
import deadlines.platform.identity.domain.RawPassword
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith

private fun password(value: String) = requireNotNull(RawPassword.of(value))

class Argon2PasswordHasherTest :
    StringSpec({
        val hasher = Argon2PasswordHasher()

        "produces an Argon2id hash" {
            hasher.hash(password("correct horse battery")).value shouldStartWith "\$argon2id\$"
        }

        "accepts the password it hashed" {
            val hash = hasher.hash(password("correct horse battery"))

            hasher.verify(password("correct horse battery"), hash) shouldBe true
        }

        "rejects a different password" {
            val hash = hasher.hash(password("correct horse battery"))

            hasher.verify(password("wrong horse battery"), hash) shouldBe false
        }

        "salts every hash, so the same password never repeats" {
            hasher.hash(password("same password")) shouldNotBe hasher.hash(password("same password"))
        }

        "rejects a malformed hash instead of throwing" {
            hasher.verify(password("correct horse battery"), PasswordHash("not-a-hash")) shouldBe false
        }

        "never reveals the hash when printed" {
            hasher.hash(password("correct horse battery")).toString() shouldBe "PasswordHash(hidden)"
        }
    })
