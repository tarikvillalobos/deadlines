package deadlines.core.paging

data class Page<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
) {
    val totalPages: Int get() = if (total == 0L) 0 else ((total - 1) / size + 1).toInt()
}
