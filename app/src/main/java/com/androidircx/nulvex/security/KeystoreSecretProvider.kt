package com.androidircx.nulvex.security

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties

class KeystoreSecretProvider(
    private val profile: VaultProfile = VaultProfile.REAL
) {
    private val prefsName = profile.keystorePrefsName
    private val alias = profile.keystoreAlias
    private val keyIv = "secret_iv"
    private val keyCt = "secret_ct"

    fun getOrCreateSecret(context: Context): ByteArray {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val storedIv = prefs.getString(keyIv, null)
        val storedCt = prefs.getString(keyCt, null)
        val secretKey = getOrCreateAesKey()

        if (storedIv != null && storedCt != null) {
            return decrypt(secretKey, storedIv, storedCt)
        }

        val secret = ByteArray(32)
        SecureRandom().nextBytes(secret)
        val (iv, ct) = encrypt(secretKey, secret)
        prefs.edit {
            putString(keyIv, iv)
            putString(keyCt, ct)
        }
        return secret
    }

    fun deleteSecret() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
        }
    }

    private fun getOrCreateAesKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val existing = (keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry)?.secretKey
        if (existing != null) return existing

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        generator.init(spec)
        return generator.generateKey()
    }

    private fun encrypt(key: SecretKey, secret: ByteArray): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ct = cipher.doFinal(secret)
        return Base64.encodeToString(iv, Base64.NO_WRAP) to
            Base64.encodeToString(ct, Base64.NO_WRAP)
    }

    private fun decrypt(key: SecretKey, ivB64: String, ctB64: String): ByteArray {
        val iv = Base64.decode(ivB64, Base64.NO_WRAP)
        val ct = Base64.decode(ctB64, Base64.NO_WRAP)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(ct)
    }
}
