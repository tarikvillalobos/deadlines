package deadlines.contracts.version

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class ApiVersionsTest :
    StringSpec({
        "versions are numbered sequentially from 1" {
            ApiVersions.all.map { it.number } shouldBe (1..ApiVersions.all.size).toList()
        }

        "latest is the highest version" {
            ApiVersions.latest shouldBe ApiVersions.all.max()
        }

        "every change refers to a known module" {
            ApiVersions.all.flatMap { it.changes.keys }.all { it in ApiModule.entries } shouldBe true
        }

        "missing header resolves to the latest version" {
            ApiVersions.parse(null) shouldBe ApiVersions.latest
            ApiVersions.parse("") shouldBe ApiVersions.latest
            ApiVersions.parse("  ") shouldBe ApiVersions.latest
        }

        "known numbers resolve to their version" {
            ApiVersions.parse("1") shouldBe ApiVersions.v1
            ApiVersions.parse(" 1 ") shouldBe ApiVersions.v1
        }

        "unknown or malformed numbers resolve to nothing" {
            ApiVersions.parse("99").shouldBeNull()
            ApiVersions.parse("abc").shouldBeNull()
            ApiVersions.parse("1.0").shouldBeNull()
        }
    })
