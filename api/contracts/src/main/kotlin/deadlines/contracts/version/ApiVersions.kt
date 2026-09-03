package deadlines.contracts.version

/**
 * Registry of every API version ever published. Clients pin one number through the
 * [HEADER]; a new entry is added only when a module breaks its contract, and the
 * [ApiVersion.changes] map records which modules changed and why.
 */
object ApiVersions {
    const val HEADER = "Deadlines-Version"

    val v1 = ApiVersion(number = 1, changes = emptyMap())

    val all: List<ApiVersion> = listOf(v1)

    val latest: ApiVersion = all.last()

    fun parse(raw: String?): ApiVersion? {
        if (raw.isNullOrBlank()) return latest
        val number = raw.trim().toIntOrNull() ?: return null
        return all.firstOrNull { it.number == number }
    }
}
