package com.androidircx.nulvex.pro

import android.util.Base64
import com.androidircx.nulvex.crypto.XChaCha20Poly1305NoteCrypto
import org.json.JSONObject
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object KeyManagerBackupCodec {
    private val crypto = XChaCha20Poly1305NoteCrypto()

    fun encryptWithPassword(plaintext: ByteArray, password: CharArray): ByteArray {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val key = derivePasswordKey(password, salt)
        val cipher = crypto.encrypt(plaintext, key)
        key.fill(0)
        val wrapper = JSONObject().apply {
            put("v", 1)
            put("alg", "xchacha20poly1305+pbkdf2")
            put("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
            put("iter", PBKDF2_ITERATIONS)
            put("ct", Base64.encodeToString(cipher, Base64.NO_WRAP))
        }
        salt.fill(0)
        cipher.fill(0)
        return wrapper.toString().toByteArray(Charsets.UTF_8)
    }

    fun decryptWithPassword(payload: ByteArray, password: CharArray): ByteArray {
        val root = JSONObject(payload.toString(Charsets.UTF_8))
        val salt = Base64.decode(root.getString("salt"), Base64.DEFAULT)
        val ct = Base64.decode(root.getString("ct"), Base64.DEFAULT)
        val key = derivePasswordKey(password, salt, root.optInt("iter", PBKDF2_ITERATIONS))
        val plaintext = crypto.decrypt(ct, key)
        key.fill(0)
        salt.fill(0)
        ct.fill(0)
        return plaintext
    }

    private fun derivePasswordKey(password: CharArray, salt: ByteArray, iterations: Int = PBKDF2_ITERATIONS): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private const val PBKDF2_ITERATIONS = 120_000
}
