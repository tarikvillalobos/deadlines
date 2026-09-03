package deadlines.platform.identity.infrastructure

import com.password4j.Argon2Function
import com.password4j.Password
import com.password4j.types.Argon2
import deadlines.platform.identity.application.PasswordHasher
import deadlines.platform.identity.domain.PasswordHash
import deadlines.platform.identity.domain.RawPassword
import org.slf4j.LoggerFactory

private const val MEMORY_KIB = 65_536
private const val ITERATIONS = 3
private const val PARALLELISM = 1
private const val OUTPUT_LENGTH = 32

class Argon2PasswordHasher : PasswordHasher {
    private val function = Argon2Function.getInstance(MEMORY_KIB, ITERATIONS, PARALLELISM, OUTPUT_LENGTH, Argon2.ID)
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun hash(password: RawPassword) =
        PasswordHash(
            Password
                .hash(password.value)
                .addRandomSalt()
                .with(function)
                .result,
        )

    override fun verify(password: RawPassword, hash: PasswordHash) =
        runCatching { Password.check(password.value, hash.value).with(function) }
            .onFailure { logger.warn("Rejecting a password hash that could not be read", it) }
            .getOrDefault(false)
}
