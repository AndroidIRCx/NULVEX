package com.androidircx.nulvex.crypto

interface NoteCrypto {
    fun encrypt(plaintext: ByteArray, key: ByteArray): ByteArray
    fun decrypt(blob: ByteArray, key: ByteArray): ByteArray
}
