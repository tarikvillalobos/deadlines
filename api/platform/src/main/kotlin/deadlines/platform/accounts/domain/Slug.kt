package deadlines.platform.accounts.domain

import java.text.Normalizer

private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")
private val EDGE_HYPHENS = Regex("^-+|-+$")
private val DIACRITICS = Regex("\\p{M}+")
private const val MAX_LENGTH = 48

@JvmInline
value class Slug private constructor(val value: String) {
    override fun toString() = value

    fun withSuffix(suffix: Int) = Slug("${value.take(MAX_LENGTH - suffix.toString().length - 1)}-$suffix")

    companion object {
        const val FALLBACK = "account"

        fun from(raw: String): Slug {
            val ascii = Normalizer.normalize(raw, Normalizer.Form.NFD).replace(DIACRITICS, "")
            val slug =
                ascii
                    .lowercase()
                    .replace(NON_ALPHANUMERIC, "-")
                    .replace(EDGE_HYPHENS, "")
                    .take(MAX_LENGTH)
                    .replace(EDGE_HYPHENS, "")
            return Slug(slug.ifBlank { FALLBACK })
        }

        fun of(value: String) = Slug(value)
    }
}
