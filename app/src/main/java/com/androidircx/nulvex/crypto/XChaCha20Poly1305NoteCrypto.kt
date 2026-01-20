package com.androidircx.nulvex.crypto

import com.google.crypto.tink.subtle.XChaCha20Poly1305

class XChaCha20Poly1305NoteCrypto : NoteCrypto {
    override fun encrypt(plaintext: ByteArray, key: ByteArray): ByteArray {
        val aead = XChaCha20Poly1305(key)
        return aead.encrypt(plaintext, byteArrayOf())
    }

    override fun decrypt(blob: ByteArray, key: ByteArray): ByteArray {
        val aead = XChaCha20Poly1305(key)
        return aead.decrypt(blob, byteArrayOf())
    }
}
