package deadlines.platform.persistence.application

data class DatabaseSettings(
    val url: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int = DEFAULT_MAX_POOL_SIZE,
) {
    companion object {
        const val DEFAULT_MAX_POOL_SIZE = 10

        fun fromEnvironment(read: (String) -> String? = System::getenv) =
            DatabaseSettings(
                url = read("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/deadlines",
                user = read("DATABASE_USER") ?: "deadlines",
                password = read("DATABASE_PASSWORD") ?: "deadlines",
                maxPoolSize = read("DATABASE_MAX_POOL_SIZE")?.toIntOrNull() ?: DEFAULT_MAX_POOL_SIZE,
            )
    }
}
