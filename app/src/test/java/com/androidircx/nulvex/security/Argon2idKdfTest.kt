package com.androidircx.nulvex.security

import org.junit.Assert.*
import org.junit.Test
import org.junit.Ignore

/**
 * Argon2 tests. Note: These may need to run as instrumented tests
 * if the argon2kt library requires native code.
 */
class Argon2idKdfTest {

    private val kdf = Argon2idKdf()

    // Light params for testing (faster execution)
    private val testParams = Argon2Params(
        memoryKiB = 1024,      // 1 MB
        iterations = 1,
        parallelism = 1,
        outputLength = 32
    )

    @Test
    @Ignore("Requires Android native libraries - run as instrumented test")
    fun `derive key produces correct length`() {
        val password = "testPassword123".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val key = kdf.deriveKey(password, salt, testParams)

        assertEquals(32, key.size)
    }

    @Test
    @Ignore("Requires Android native libraries - run as instrumented test")
    fun `same inputs produce same output`() {
        val password1 = "password".toCharArray()
        val password2 = "password".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val key1 = kdf.deriveKey(password1, salt, testParams)
        val key2 = kdf.deriveKey(password2, salt, testParams)

        assertArrayEquals(key1, key2)
    }

    @Test
    @Ignore("Requires Android native libraries - run as instrumented test")
    fun `different passwords produce different outputs`() {
        val password1 = "password1".toCharArray()
        val password2 = "password2".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val key1 = kdf.deriveKey(password1, salt, testParams)
        val key2 = kdf.deriveKey(password2, salt, testParams)

        assertFalse(key1.contentEquals(key2))
    }

    @Test
    @Ignore("Requires Android native libraries - run as instrumented test")
    fun `different salts produce different outputs`() {
        val password = "password".toCharArray()
        val salt1 = ByteArray(16) { it.toByte() }
        val salt2 = ByteArray(16) { (it + 1).toByte() }

        val key1 = kdf.deriveKey(password.copyOf(), salt1, testParams)
        val key2 = kdf.deriveKey(password.copyOf(), salt2, testParams)

        assertFalse(key1.contentEquals(key2))
    }

    @Test
    @Ignore("Requires Android native libraries - run as instrumented test")
    fun `different output lengths work`() {
        val password = "password".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val params16 = testParams.copy(outputLength = 16)
        val params64 = testParams.copy(outputLength = 64)

        val key16 = kdf.deriveKey(password.copyOf(), salt, params16)
        val key64 = kdf.deriveKey(password.copyOf(), salt, params64)

        assertEquals(16, key16.size)
        assertEquals(64, key64.size)
    }

    @Test
    @Ignore("Requires Android native libraries - run as instrumented test")
    fun `empty password works`() {
        val password = CharArray(0)
        val salt = ByteArray(16) { it.toByte() }

        val key = kdf.deriveKey(password, salt, testParams)

        assertEquals(32, key.size)
    }

    @Test
    @Ignore("Requires Android native libraries - run as instrumented test")
    fun `unicode password works`() {
        val password = "пароль密码🔐".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val key = kdf.deriveKey(password, salt, testParams)

        assertEquals(32, key.size)
    }
}
