package com.androidircx.nulvex.pro

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeyManagerBackupCodecTest {

    @Test
    fun encryptThenDecrypt_roundTrip() {
        val plain = "secret-manager-export".toByteArray()
        val password = "P@ssw0rd!".toCharArray()

        val encrypted = KeyManagerBackupCodec.encryptWithPassword(plain, password)
        val decrypted = KeyManagerBackupCodec.decryptWithPassword(encrypted, password)

        assertEquals("secret-manager-export", decrypted.toString(Charsets.UTF_8))
        assertTrue(encrypted.toString(Charsets.UTF_8).contains("\"alg\":\"xchacha20poly1305+pbkdf2\""))
    }

    @Test
    fun decryptWithWrongPassword_throws() {
        val plain = "secret-manager-export".toByteArray()
        val encrypted = KeyManagerBackupCodec.encryptWithPassword(plain, "correct".toCharArray())

        var failed = false
        try {
            KeyManagerBackupCodec.decryptWithPassword(encrypted, "wrong".toCharArray())
        } catch (_: Exception) {
            failed = true
        }

        assertTrue(failed)
    }
}
