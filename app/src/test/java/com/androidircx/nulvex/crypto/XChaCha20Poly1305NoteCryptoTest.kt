package com.androidircx.nulvex.crypto

import org.junit.Assert.*
import org.junit.Test
import java.security.SecureRandom

class XChaCha20Poly1305NoteCryptoTest {

    private val crypto = XChaCha20Poly1305NoteCrypto()

    private fun randomKey(): ByteArray {
        val key = ByteArray(32)
        SecureRandom().nextBytes(key)
        return key
    }

    @Test
    fun `encrypt and decrypt round trip`() {
        val key = randomKey()
        val plaintext = "Hello, Nulvex!".toByteArray()

        val ciphertext = crypto.encrypt(plaintext, key)
        val decrypted = crypto.decrypt(ciphertext, key)

        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun `encrypt produces different ciphertext each time`() {
        val key = randomKey()
        val plaintext = "Same message".toByteArray()

        val ciphertext1 = crypto.encrypt(plaintext, key)
        val ciphertext2 = crypto.encrypt(plaintext, key)

        // Ciphertexts should differ due to random nonce
        assertFalse(ciphertext1.contentEquals(ciphertext2))
    }

    @Test
    fun `ciphertext is longer than plaintext`() {
        val key = randomKey()
        val plaintext = "Test".toByteArray()

        val ciphertext = crypto.encrypt(plaintext, key)

        // XChaCha20-Poly1305 adds 24-byte nonce + 16-byte tag
        assertTrue(ciphertext.size > plaintext.size)
        assertEquals(plaintext.size + 24 + 16, ciphertext.size)
    }

    @Test
    fun `decrypt with wrong key fails`() {
        val key1 = randomKey()
        val key2 = randomKey()
        val plaintext = "Secret message".toByteArray()

        val ciphertext = crypto.encrypt(plaintext, key1)

        assertThrows(Exception::class.java) {
            crypto.decrypt(ciphertext, key2)
        }
    }

    @Test
    fun `decrypt tampered ciphertext fails`() {
        val key = randomKey()
        val plaintext = "Important data".toByteArray()

        val ciphertext = crypto.encrypt(plaintext, key)
        // Tamper with the ciphertext
        ciphertext[ciphertext.size / 2] = (ciphertext[ciphertext.size / 2].toInt() xor 0xFF).toByte()

        assertThrows(Exception::class.java) {
            crypto.decrypt(ciphertext, key)
        }
    }

    @Test
    fun `encrypt empty plaintext works`() {
        val key = randomKey()
        val plaintext = ByteArray(0)

        val ciphertext = crypto.encrypt(plaintext, key)
        val decrypted = crypto.decrypt(ciphertext, key)

        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun `encrypt large plaintext works`() {
        val key = randomKey()
        val plaintext = ByteArray(1024 * 1024) // 1 MB
        SecureRandom().nextBytes(plaintext)

        val ciphertext = crypto.encrypt(plaintext, key)
        val decrypted = crypto.decrypt(ciphertext, key)

        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun `encrypt with unicode text works`() {
        val key = randomKey()
        val plaintext = "Привет мир! 你好世界! 🔐🛡️".toByteArray(Charsets.UTF_8)

        val ciphertext = crypto.encrypt(plaintext, key)
        val decrypted = crypto.decrypt(ciphertext, key)

        assertEquals(
            String(plaintext, Charsets.UTF_8),
            String(decrypted, Charsets.UTF_8)
        )
    }
}
