package com.androidircx.nulvex.pro

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.bcpg.HashAlgorithmTags
import org.bouncycastle.openpgp.PGPEncryptedData
import org.bouncycastle.openpgp.PGPKeyRingGenerator
import org.bouncycastle.openpgp.PGPSignature
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair
import java.security.KeyStore
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.Security
import java.util.Date
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class SharedKeyInfo(
    val id: String,
    val label: String,
    val source: String,
    val format: String,
    val fingerprint: String,
    val createdAt: Long
)

class SharedKeyStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun listKeys(): List<SharedKeyInfo> {
        val array = loadArray()
        val items = mutableListOf<SharedKeyInfo>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            items.add(
                SharedKeyInfo(
                    id = obj.optString(KEY_ID),
                    label = obj.optString(KEY_LABEL, "Imported key"),
                    source = obj.optString(KEY_SOURCE, "manual"),
                    format = obj.optString(KEY_FORMAT, FORMAT_XCHACHA_KEY),
                    fingerprint = obj.optString(KEY_FINGERPRINT),
                    createdAt = obj.optLong(KEY_CREATED_AT, 0L)
                )
            )
        }
        return items.sortedByDescending { it.createdAt }
    }

    fun importKey(label: String, source: String, rawInput: String): SharedKeyInfo {
        val trimmed = rawInput.trim()
        require(trimmed.isNotBlank()) { "Key input is empty" }

        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()
        val (format, fingerprint, material) = if (trimmed.contains("BEGIN PGP", ignoreCase = true)) {
            val parsed = OpenPgpSupport.parseArmoredKey(trimmed)
            Triple(parsed.format, parsed.fingerprint, parsed.keyMaterial)
        } else {
            val keyMaterial = parseSymmetricMaterial(trimmed)
            Triple(FORMAT_XCHACHA_KEY, sha256Hex(keyMaterial).take(32), keyMaterial)
        }
        return storeKeyMaterial(
            id = id,
            label = label.ifBlank { "Imported key" },
            source = source.ifBlank { "manual" },
            format = format,
            fingerprint = fingerprint,
            createdAt = now,
            material = material
        )
    }

    fun generateXChaChaKey(label: String = "XChaCha key"): SharedKeyInfo {
        val material = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }
        val now = System.currentTimeMillis()
        return storeKeyMaterial(
            id = UUID.randomUUID().toString(),
            label = label.ifBlank { "XChaCha key" },
            source = "generated",
            format = FORMAT_XCHACHA_KEY,
            fingerprint = sha256Hex(material).take(32),
            createdAt = now,
            material = material
        )
    }

    fun generatePgpKey(label: String = "OpenPGP key"): SharedKeyInfo {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        val keyPair = generator.generateKeyPair()
        val nowDate = Date()
        val calc = JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1)
        val pgpKeyPair = JcaPGPKeyPair(org.bouncycastle.openpgp.PGPPublicKey.RSA_GENERAL, keyPair, nowDate)
        val signer = JcaPGPContentSignerBuilder(pgpKeyPair.publicKey.algorithm, HashAlgorithmTags.SHA256)
        // Keep generated secret key unencrypted in this inner PGP ring; app-level storage is
        // still encrypted in Android Keystore via storeKeyMaterial().
        val encryptor = null
        val uid = "${label.ifBlank { "OpenPGP key" }} <nulvex@local>"
        val ringGen = PGPKeyRingGenerator(
            PGPSignature.POSITIVE_CERTIFICATION,
            pgpKeyPair,
            uid,
            calc,
            null,
            null,
            signer,
            encryptor
        )
        val secretRing = ringGen.generateSecretKeyRing()
        val publicFingerprint = secretRing.secretKey.publicKey.fingerprint.joinToString("") { "%02x".format(it) }
        val material = secretRing.encoded
        val created = System.currentTimeMillis()
        return storeKeyMaterial(
            id = UUID.randomUUID().toString(),
            label = label.ifBlank { "OpenPGP key" },
            source = "generated",
            format = "pgp_secret",
            fingerprint = publicFingerprint,
            createdAt = created,
            material = material
        )
    }

    fun deleteKey(id: String): Boolean {
        val current = loadArray()
        val updated = JSONArray()
        var removed = false
        for (i in 0 until current.length()) {
            val obj = current.optJSONObject(i) ?: continue
            if (obj.optString(KEY_ID) == id) {
                removed = true
            } else {
                updated.put(obj)
            }
        }
        if (removed) saveArray(updated)
        return removed
    }

    fun getKeyMaterial(id: String): ByteArray? {
        val array = loadArray()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            if (obj.optString(KEY_ID) != id) continue
            val iv = obj.optString(KEY_IV, "")
            val ct = obj.optString(KEY_CT, "")
            if (iv.isBlank() || ct.isBlank()) return null
            return decrypt(iv, ct)
        }
        return null
    }

    fun buildTransferPayload(id: String): String? {
        val array = loadArray()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            if (obj.optString(KEY_ID) != id) continue
            val material = getKeyMaterial(id) ?: return null
            val payload = JSONObject().apply {
                put("v", 1)
                put("type", "nulvex-key-share")
                put("label", obj.optString(KEY_LABEL, "Shared key"))
                put("source", "transfer")
                put("format", obj.optString(KEY_FORMAT, FORMAT_XCHACHA_KEY))
                put("fingerprint", obj.optString(KEY_FINGERPRINT, ""))
                put("material_b64", Base64.encodeToString(material, Base64.NO_WRAP))
            }
            material.fill(0)
            return payload.toString()
        }
        return null
    }

    fun importTransferPayload(payload: String, source: String = "qr"): SharedKeyInfo {
        val root = JSONObject(payload)
        require(root.optString("type") == "nulvex-key-share") { "Invalid key share payload" }
        val label = root.optString("label", "Shared key")
        val format = root.optString("format", FORMAT_XCHACHA_KEY)
        val fingerprint = root.optString("fingerprint", "")
        val materialB64 = root.optString("material_b64", "")
        require(materialB64.isNotBlank()) { "Missing key material" }
        val material = Base64.decode(materialB64, Base64.DEFAULT)
        return storeKeyMaterial(
            id = UUID.randomUUID().toString(),
            label = label,
            source = source,
            format = format,
            fingerprint = if (fingerprint.isBlank()) sha256Hex(material).take(32) else fingerprint,
            createdAt = System.currentTimeMillis(),
            material = material
        )
    }

    fun exportManagerBackup(encrypted: Boolean, password: CharArray?): ByteArray {
        val keys = loadArray()
        val export = JSONObject().apply {
            put("v", 1)
            put("type", "nulvex-key-manager")
            put("exportedAt", System.currentTimeMillis())
            put("items", JSONArray())
        }
        val out = export.getJSONArray("items")
        for (i in 0 until keys.length()) {
            val obj = keys.optJSONObject(i) ?: continue
            val id = obj.optString(KEY_ID, "")
            val iv = obj.optString(KEY_IV, "")
            val ct = obj.optString(KEY_CT, "")
            if (id.isBlank() || iv.isBlank() || ct.isBlank()) continue
            val material = decrypt(iv, ct)
            out.put(
                JSONObject().apply {
                    put(KEY_ID, id)
                    put(KEY_LABEL, obj.optString(KEY_LABEL, "Imported key"))
                    put(KEY_SOURCE, obj.optString(KEY_SOURCE, "manual"))
                    put(KEY_FORMAT, obj.optString(KEY_FORMAT, FORMAT_XCHACHA_KEY))
                    put(KEY_FINGERPRINT, obj.optString(KEY_FINGERPRINT, ""))
                    put(KEY_CREATED_AT, obj.optLong(KEY_CREATED_AT, System.currentTimeMillis()))
                    put("material_b64", Base64.encodeToString(material, Base64.NO_WRAP))
                }
            )
            material.fill(0)
        }
        val plain = export.toString().toByteArray(Charsets.UTF_8)
        if (!encrypted) return plain
        require(password != null && password.isNotEmpty()) { "Password required for encrypted export" }
        val encryptedBytes = KeyManagerBackupCodec.encryptWithPassword(plain, password)
        plain.fill(0)
        return encryptedBytes
    }

    fun importManagerBackup(payload: ByteArray, password: CharArray? = null): Int {
        val plaintext = try {
            val asText = payload.toString(Charsets.UTF_8)
            if (asText.contains("\"alg\":\"xchacha20poly1305+pbkdf2\"")) {
                require(password != null && password.isNotEmpty()) { "Password is required to import encrypted key manager backup" }
                KeyManagerBackupCodec.decryptWithPassword(payload, password)
            } else {
                payload
            }
        } catch (_: Exception) {
            payload
        }
        val root = JSONObject(plaintext.toString(Charsets.UTF_8))
        val array = root.optJSONArray("items") ?: JSONArray()
        var imported = 0
        val current = loadArray()
        val existingById = mutableMapOf<String, JSONObject>()
        for (i in 0 until current.length()) {
            val obj = current.optJSONObject(i) ?: continue
            existingById[obj.optString(KEY_ID)] = obj
        }

        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val id = item.optString(KEY_ID, "").ifBlank { UUID.randomUUID().toString() }
            val materialB64 = item.optString("material_b64", "")
            if (materialB64.isBlank()) continue
            val material = Base64.decode(materialB64, Base64.DEFAULT)
            val (ivB64, ctB64) = encrypt(material)
            material.fill(0)

            val merged = JSONObject().apply {
                put(KEY_ID, id)
                put(KEY_LABEL, item.optString(KEY_LABEL, "Imported key"))
                put(KEY_SOURCE, item.optString(KEY_SOURCE, "imported"))
                put(KEY_FORMAT, item.optString(KEY_FORMAT, FORMAT_XCHACHA_KEY))
                put(KEY_FINGERPRINT, item.optString(KEY_FINGERPRINT, ""))
                put(KEY_CREATED_AT, item.optLong(KEY_CREATED_AT, System.currentTimeMillis()))
                put(KEY_IV, ivB64)
                put(KEY_CT, ctB64)
            }
            existingById[id] = merged
            imported++
        }

        val updated = JSONArray()
        existingById.values.forEach { updated.put(it) }
        saveArray(updated)
        return imported
    }

    private fun loadArray(): JSONArray {
        val raw = prefs.getString(KEY_ITEMS, null) ?: return JSONArray()
        return try {
            JSONArray(raw)
        } catch (_: Exception) {
            JSONArray()
        }
    }

    private fun saveArray(array: JSONArray) {
        prefs.edit { putString(KEY_ITEMS, array.toString()) }
    }

    private fun storeKeyMaterial(
        id: String,
        label: String,
        source: String,
        format: String,
        fingerprint: String,
        createdAt: Long,
        material: ByteArray
    ): SharedKeyInfo {
        val (ivB64, ctB64) = encrypt(material)
        material.fill(0)
        val item = JSONObject().apply {
            put(KEY_ID, id)
            put(KEY_LABEL, label)
            put(KEY_SOURCE, source)
            put(KEY_FORMAT, format)
            put(KEY_FINGERPRINT, fingerprint)
            put(KEY_CREATED_AT, createdAt)
            put(KEY_IV, ivB64)
            put(KEY_CT, ctB64)
        }
        val array = loadArray()
        array.put(item)
        saveArray(array)
        return SharedKeyInfo(
            id = id,
            label = label,
            source = source,
            format = format,
            fingerprint = fingerprint,
            createdAt = createdAt
        )
    }

    private fun parseSymmetricMaterial(input: String): ByteArray {
        val compact = input.replace("\\s".toRegex(), "")

        val maybeBase64 = tryDecodeBase64(compact)
        if (maybeBase64 != null) {
            require(maybeBase64.size == 32) { "XChaCha key must be exactly 32 bytes (base64-decoded)" }
            return maybeBase64
        }

        val maybeHex = tryDecodeHex(compact)
        if (maybeHex != null) {
            require(maybeHex.size == 32) { "XChaCha key must be exactly 32 bytes (hex-decoded)" }
            return maybeHex
        }
        throw IllegalArgumentException("Invalid XChaCha key format. Use 32-byte base64 or 64-char hex.")
    }

    private fun tryDecodeBase64(value: String): ByteArray? {
        if (value.length < 16) return null
        return try {
            Base64.decode(value, Base64.DEFAULT)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun tryDecodeHex(value: String): ByteArray? {
        if (value.length < 32 || value.length % 2 != 0) return null
        if (!value.matches(Regex("^[0-9a-fA-F]+$"))) return null
        val out = ByteArray(value.length / 2)
        var i = 0
        while (i < value.length) {
            val byte = value.substring(i, i + 2).toInt(16).toByte()
            out[i / 2] = byte
            i += 2
        }
        return out
    }

    private fun sha256Hex(input: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input)
        return digest.joinToString(separator = "") { "%02x".format(it) }
    }

    private fun encrypt(plaintext: ByteArray): Pair<String, String> {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv
        val ct = cipher.doFinal(plaintext)
        return Base64.encodeToString(iv, Base64.NO_WRAP) to Base64.encodeToString(ct, Base64.NO_WRAP)
    }

    private fun decrypt(ivB64: String, ctB64: String): ByteArray {
        val iv = Base64.decode(ivB64, Base64.DEFAULT)
        val ct = Base64.decode(ctB64, Base64.DEFAULT)
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(128, iv))
        return cipher.doFinal(ct)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val store = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        val existing = (store.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
        if (existing != null) return existing

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    companion object {
        private const val PREFS_NAME = "nulvex_shared_keys"
        private const val KEY_ITEMS = "items"

        private const val KEY_ID = "id"
        private const val KEY_LABEL = "label"
        private const val KEY_SOURCE = "source"
        private const val KEY_FORMAT = "format"
        private const val KEY_FINGERPRINT = "fingerprint"
        private const val KEY_CREATED_AT = "createdAt"
        private const val KEY_IV = "iv"
        private const val KEY_CT = "ct"

        private const val FORMAT_XCHACHA_KEY = "xchacha20poly1305_key"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "nulvex_shared_keys_aes"
        private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
