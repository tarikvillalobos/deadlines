package deadlines.platform.identity.domain

private val EMAIL_PATTERN = Regex("^[^@\\s]+@[^@\\s.]+\\.[^@\\s]+$")
private const val MAX_LENGTH = 254

@JvmInline
value class Email private constructor(val value: String) {
    override fun toString() = value

    companion object {
        fun of(raw: String): Email? {
            val normalised = raw.trim().lowercase()
            return if (normalised.length <= MAX_LENGTH && EMAIL_PATTERN.matches(normalised)) Email(normalised) else null
        }
    }
}
