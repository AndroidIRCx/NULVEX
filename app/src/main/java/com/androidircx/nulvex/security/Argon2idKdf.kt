package com.androidircx.nulvex.security

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import com.androidircx.nulvex.security.wipe

class Argon2idKdf : Kdf {
    private val argon2 = Argon2Kt()

    override fun deriveKey(
        password: CharArray,
        salt: ByteArray,
        params: Argon2Params
    ): ByteArray {
        val passwordBytes = password.concatToString().toByteArray(Charsets.UTF_8)
        val result = try {
            argon2.hash(
                mode = Argon2Mode.ARGON2_ID,
                password = passwordBytes,
                salt = salt,
                tCostInIterations = params.iterations,
                mCostInKibibyte = params.memoryKiB,
                parallelism = params.parallelism,
                hashLengthInBytes = params.outputLength
            )
        } finally {
            passwordBytes.fill(0)
            password.wipe()
        }
        return result.rawHashAsByteArray()
    }
}
