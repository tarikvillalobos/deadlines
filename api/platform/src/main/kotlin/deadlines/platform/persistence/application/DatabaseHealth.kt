package deadlines.platform.persistence.application

fun interface DatabaseHealth {
    suspend fun isReachable(): Boolean
}
