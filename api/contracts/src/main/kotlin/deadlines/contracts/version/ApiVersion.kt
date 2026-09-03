package deadlines.contracts.version

data class ApiVersion(val number: Int, val changes: Map<ApiModule, String>) : Comparable<ApiVersion> {
    override fun compareTo(other: ApiVersion) = number.compareTo(other.number)

    override fun toString() = number.toString()
}
