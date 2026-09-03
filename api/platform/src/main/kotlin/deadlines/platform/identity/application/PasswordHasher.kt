package deadlines.platform.identity.application

import deadlines.platform.identity.domain.PasswordHash
import deadlines.platform.identity.domain.RawPassword

interface PasswordHasher {
    fun hash(password: RawPassword): PasswordHash

    fun verify(password: RawPassword, hash: PasswordHash): Boolean
}
