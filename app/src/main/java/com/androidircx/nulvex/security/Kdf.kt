package com.androidircx.nulvex.security

interface Kdf {
    fun deriveKey(password: CharArray, salt: ByteArray, params: Argon2Params): ByteArray
}
