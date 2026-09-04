package deadlines.platform.identity.domain

@JvmInline
value class PasswordHash(val value: String) {
    override fun toString() = "PasswordHash(hidden)"
}
