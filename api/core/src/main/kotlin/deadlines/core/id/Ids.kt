package deadlines.core.id

import com.fasterxml.uuid.Generators
import java.util.UUID

/** Generates time-ordered UUIDv7 identifiers, so primary keys stay index friendly. */
object Ids {
    private val generator = Generators.timeBasedEpochGenerator()

    fun next(): UUID = generator.generate()
}
