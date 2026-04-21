package com.androidircx.nulvex.security

import android.content.Context
import android.content.SharedPreferences
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import org.json.JSONArray
import java.util.Base64

internal class SecureTypedPreferences private constructor(
    private val prefs: SharedPreferences,
    private val aead: Aead
) {
    fun contains(key: String): Boolean = prefs.contains(key)

    fun getString(key: String, defaultValue: String?): String? {
        return (getValue(key) as? String) ?: defaultValue
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return (getValue(key) as? Long) ?: defaultValue
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return (getValue(key) as? Int) ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return (getValue(key) as? Boolean) ?: defaultValue
    }

    fun putString(key: String, value: String?) {
        if (value == null) {
            remove(key)
            return
        }
        putValue(key, value)
    }

    fun putLong(key: String, value: Long) = putValue(key, value)

    fun putInt(key: String, value: Int) = putValue(key, value)

    fun putBoolean(key: String, value: Boolean) = putValue(key, value)

    fun remove(vararg keys: String) {
        val editor = prefs.edit()
        keys.forEach { key -> editor.remove(key) }
        editor.apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun putAll(values: Map<String, *>, overwriteExisting: Boolean = true): Boolean {
        val editor = prefs.edit()
        var changed = false
        values.forEach { (key, value) ->
            if (key.isBlank() || value == null) return@forEach
            if (!overwriteExisting && prefs.contains(key)) return@forEach
            val encoded = encodeValue(value) ?: return@forEach
            val encrypted = encrypt(key, encoded) ?: return@forEach
            editor.putString(key, encrypted)
            changed = true
        }
        return if (!changed) true else editor.commit()
    }

    fun getAll(): Map<String, Any> {
        val result = linkedMapOf<String, Any>()
        prefs.all.forEach { (key, raw) ->
            val encrypted = raw as? String ?: return@forEach
            val encoded = decrypt(key, encrypted) ?: return@forEach
            val decoded = decodeValue(encoded) ?: return@forEach
            result[key] = decoded
        }
        return result
    }

    private fun putValue(key: String, value: Any) {
        val encoded = encodeValue(value) ?: return
        val encrypted = encrypt(key, encoded) ?: return
        prefs.edit().putString(key, encrypted).apply()
    }

    private fun getValue(key: String): Any? {
        val encrypted = prefs.getString(key, null) ?: return null
        val encoded = decrypt(key, encrypted) ?: return null
        return decodeValue(encoded)
    }

    private fun encrypt(key: String, plaintext: String): String? {
        return runCatching {
            val ciphertext = aead.encrypt(
                plaintext.toByteArray(Charsets.UTF_8),
                aadForKey(key)
            )
            Base64.getEncoder().encodeToString(ciphertext)
        }.getOrNull()
    }

    private fun decrypt(key: String, ciphertextB64: String): String? {
        return runCatching {
            val ciphertext = Base64.getDecoder().decode(ciphertextB64)
            val plaintext = aead.decrypt(ciphertext, aadForKey(key))
            plaintext.toString(Charsets.UTF_8)
        }.getOrNull()
    }

    private fun aadForKey(key: String): ByteArray {
        return ("nulvex-pref-v2:$key").toByteArray(Charsets.UTF_8)
    }

    private fun encodeValue(value: Any): String? {
        return when (value) {
            is String -> "$TYPE_STRING${base64(value)}"
            is Long -> "$TYPE_LONG$value"
            is Int -> "$TYPE_INT$value"
            is Boolean -> "$TYPE_BOOLEAN${if (value) "1" else "0"}"
            is Float -> "$TYPE_FLOAT$value"
            is Set<*> -> {
                if (!value.all { it is String }) return null
                val arr = JSONArray()
                value.filterIsInstance<String>().forEach { item -> arr.put(item) }
                "$TYPE_STRING_SET${base64(arr.toString())}"
            }

            else -> null
        }
    }

    private fun decodeValue(encoded: String): Any? {
        return when {
            encoded.startsWith(TYPE_STRING) -> decodeBase64(encoded.removePrefix(TYPE_STRING))
            encoded.startsWith(TYPE_LONG) -> encoded.removePrefix(TYPE_LONG).toLongOrNull()
            encoded.startsWith(TYPE_INT) -> encoded.removePrefix(TYPE_INT).toIntOrNull()
            encoded.startsWith(TYPE_BOOLEAN) -> when (encoded.removePrefix(TYPE_BOOLEAN)) {
                "1" -> true
                "0" -> false
                else -> null
            }

            encoded.startsWith(TYPE_FLOAT) -> encoded.removePrefix(TYPE_FLOAT).toFloatOrNull()
            encoded.startsWith(TYPE_STRING_SET) -> {
                val raw = decodeBase64(encoded.removePrefix(TYPE_STRING_SET)) ?: return null
                runCatching {
                    val arr = JSONArray(raw)
                    buildSet {
                        for (idx in 0 until arr.length()) {
                            add(arr.getString(idx))
                        }
                    }
                }.getOrNull()
            }

            else -> null
        }
    }

    private fun base64(value: String): String {
        return Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
    }

    private fun decodeBase64(value: String): String? {
        return runCatching {
            val bytes = Base64.getDecoder().decode(value)
            bytes.toString(Charsets.UTF_8)
        }.getOrNull()
    }

    companion object {
        private const val TYPE_STRING = "s:"
        private const val TYPE_LONG = "l:"
        private const val TYPE_INT = "i:"
        private const val TYPE_BOOLEAN = "b:"
        private const val TYPE_FLOAT = "f:"
        private const val TYPE_STRING_SET = "ss:"
        private const val MASTER_KEY_URI = "android-keystore://nulvex_tink_master_key_v2"
        private const val KEYSET_PREF_SUFFIX = "_tink_keyset"
        private const val KEYSET_ALIAS = "__nulvex_tink_keyset__"

        fun create(context: Context, prefsName: String): SecureTypedPreferences? {
            return runCatching {
                val appContext = context.applicationContext
                AeadConfig.register()
                val keysetManager = AndroidKeysetManager.Builder()
                    .withSharedPref(appContext, KEYSET_ALIAS, "$prefsName$KEYSET_PREF_SUFFIX")
                    .withMasterKeyUri(MASTER_KEY_URI)
                    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                    .build()
                val aead = keysetManager.getKeysetHandle().getPrimitive(
                    RegistryConfiguration.get(),
                    Aead::class.java
                )
                val prefs = appContext.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                SecureTypedPreferences(prefs, aead)
            }.getOrNull()
        }
    }
}
