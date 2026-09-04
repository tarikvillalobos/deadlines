package deadlines.core.id

import com.fasterxml.uuid.Generators
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

/** Generates time-ordered UUIDv7 identifiers, so primary keys stay index friendly. */
object Ids {
    private val generator = Generators.timeBasedEpochGenerator()

    fun next(): Uuid = generator.generate().toKotlinUuid()
}
