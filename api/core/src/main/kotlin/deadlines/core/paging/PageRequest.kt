package deadlines.core.paging

data class PageRequest(val page: Int = 1, val size: Int = DEFAULT_SIZE) {
    init {
        require(page >= 1) { "page must be at least 1" }
        require(size in 1..MAX_SIZE) { "size must be between 1 and $MAX_SIZE" }
    }

    val offset: Long get() = (page - 1L) * size

    companion object {
        const val DEFAULT_SIZE = 20
        const val MAX_SIZE = 100
    }
}
