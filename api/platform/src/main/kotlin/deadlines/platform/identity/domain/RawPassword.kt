package deadlines.platform.identity.domain

private const val MIN_LENGTH = 8
private const val MAX_LENGTH = 128

/** A password as typed by a person, validated but not yet hashed. */
@JvmInline
value class RawPassword private constructor(val value: String) {
    override fun toString() = "RawPassword(hidden)"

    companion object {
        const val MINIMUM_LENGTH = MIN_LENGTH
        const val MAXIMUM_LENGTH = MAX_LENGTH

        fun of(raw: String): RawPassword? = if (raw.length in MIN_LENGTH..MAX_LENGTH) RawPassword(raw) else null
    }
}
