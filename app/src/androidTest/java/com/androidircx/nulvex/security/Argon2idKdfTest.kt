package com.androidircx.nulvex.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Argon2idKdfTest {

    private val kdf = Argon2idKdf()

    // Light params for faster execution on device
    private val testParams = Argon2Params(
        memoryKiB = 1024,
        iterations = 1,
        parallelism = 1,
        outputLength = 32
    )

    @Test
    fun deriveKeyProducesCorrectLength() {
        val password = "testPassword123".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val key = kdf.deriveKey(password, salt, testParams)

        assertEquals(32, key.size)
    }

    @Test
    fun sameInputsProduceSameOutput() {
        val password1 = "password".toCharArray()
        val password2 = "password".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val key1 = kdf.deriveKey(password1, salt, testParams)
        val key2 = kdf.deriveKey(password2, salt, testParams)

        assertArrayEquals(key1, key2)
    }

    @Test
    fun differentPasswordsProduceDifferentOutputs() {
        val password1 = "password1".toCharArray()
        val password2 = "password2".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val key1 = kdf.deriveKey(password1, salt, testParams)
        val key2 = kdf.deriveKey(password2, salt, testParams)

        assertFalse(key1.contentEquals(key2))
    }

    @Test
    fun differentSaltsProduceDifferentOutputs() {
        val password = "password".toCharArray()
        val salt1 = ByteArray(16) { it.toByte() }
        val salt2 = ByteArray(16) { (it + 1).toByte() }

        val key1 = kdf.deriveKey(password.copyOf(), salt1, testParams)
        val key2 = kdf.deriveKey(password.copyOf(), salt2, testParams)

        assertFalse(key1.contentEquals(key2))
    }

    @Test
    fun differentOutputLengthsWork() {
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
    fun emptyPasswordWorks() {
        val password = CharArray(0)
        val salt = ByteArray(16) { it.toByte() }

        val key = kdf.deriveKey(password, salt, testParams)

        assertEquals(32, key.size)
    }

    @Test
    fun unicodePasswordWorks() {
        val password = "пароль密码🔐".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        val key = kdf.deriveKey(password, salt, testParams)

        assertEquals(32, key.size)
    }

    @Test
    fun passwordArrayIsWipedAfterDerivation() {
        val password = "secret".toCharArray()
        val salt = ByteArray(16) { it.toByte() }

        kdf.deriveKey(password, salt, testParams)

        // After derivation the password array must be zeroed
        assertTrue(password.all { it == '\u0000' })
    }
}
