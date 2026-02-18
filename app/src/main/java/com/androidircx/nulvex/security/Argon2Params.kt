package com.androidircx.nulvex.security

data class Argon2Params(
    val memoryKiB: Int,
    val iterations: Int,
    val parallelism: Int,
    val outputLength: Int
) {
    companion object {
        val DEFAULT = Argon2Params(
            memoryKiB = 262_144,
            iterations = 3,
            parallelism = 2,
            outputLength = 32
        )
    }
}
