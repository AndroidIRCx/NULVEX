package com.androidircx.nulvex.pro

import com.androidircx.nulvex.crypto.XChaCha20Poly1305NoteCrypto
import com.androidircx.nulvex.data.VaultService
import com.androidircx.nulvex.security.Hkdf
import com.androidircx.nulvex.security.wipe
import org.json.JSONObject
import java.security.MessageDigest
import android.util.Base64

data class BackupUploadResult(
    val mediaId: String,
    val sizeBytes: Int,
    val sha256: String
)

data class NoteShareUploadResult(
    val mediaId: String,
    val downloadPathId: String,
    val url: String
)

class EncryptedBackupService(
    private val vaultService: VaultService,
    private val sharedKeyStore: SharedKeyStore,
    private val backupRegistryStore: BackupRegistryStore,
    private val apiClient: LaravelMediaApiClient = LaravelMediaApiClient()
) {
    private val noteCrypto = XChaCha20Poly1305NoteCrypto()

    suspend fun uploadEncryptedBackup(keyId: String): BackupUploadResult {
        val wrapperBytes = buildEncryptedBackupWrapper(keyId)
        val token = apiClient.requestUpload(type = "file", mime = "application/octet-stream")
        apiClient.upload(token.id, token.uploadToken, token.expires, wrapperBytes)
        val result = BackupUploadResult(
            mediaId = token.id,
            sizeBytes = wrapperBytes.size,
            sha256 = sha256Hex(wrapperBytes)
        )
        backupRegistryStore.add(
            mediaId = result.mediaId,
            downloadPathId = token.downloadToken ?: result.mediaId,
            keyId = keyId,
            sizeBytes = result.sizeBytes,
            sha256 = result.sha256,
            downloadToken = token.downloadToken,
            downloadExpires = token.downloadExpires
        )
        return result
    }

    suspend fun uploadEncryptedNoteShare(noteId: String, keyId: String): NoteShareUploadResult {
        val wrapper = buildEncryptedNoteShareWrapper(noteId, keyId)
        val token = apiClient.requestUpload(type = "file", mime = NulvexFileTypes.NOTE_SHARE_MIME)
        apiClient.upload(token.id, token.uploadToken, token.expires, wrapper)
        val pathId = token.downloadToken ?: token.id
        return NoteShareUploadResult(
            mediaId = token.id,
            downloadPathId = pathId,
            url = "https://androidircx.com/api/media/download/$pathId"
        )
    }

    suspend fun buildEncryptedNoteShareWrapper(noteId: String, keyId: String): ByteArray {
        val keyMaterial = sharedKeyStore.getKeyMaterial(keyId)
            ?: throw IllegalArgumentException("Shared key not found")
        val noteKey = deriveBackupKey(keyMaterial)
        keyMaterial.wipe()

        val noteJson = vaultService.exportSingleNoteShareJsonBytes(noteId)
        val encrypted = noteCrypto.encrypt(noteJson, noteKey)
        noteKey.wipe()
        noteJson.fill(0)

        val payloadB64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        encrypted.fill(0)

        val wrapper = JSONObject().apply {
            put("v", 1)
            put("kind", "note-share")
            put("ext", NulvexFileTypes.NOTE_SHARE_EXT)
            put("alg", "xchacha20poly1305")
            put("key_id", keyId)
            put("payload", payloadB64)
        }.toString().toByteArray(Charsets.UTF_8)
        return wrapper
    }

    suspend fun restoreFromRemote(
        mediaId: String,
        keyId: String,
        merge: Boolean,
        downloadToken: String? = null,
        downloadExpires: Long? = null
    ): Int {
        val wrapper = apiClient.download(mediaId, downloadToken, downloadExpires)
        val plaintext = decryptWrapper(wrapper, keyId)
        try {
            return vaultService.importBackupJsonBytes(plaintext, merge = merge)
        } finally {
            plaintext.fill(0)
        }
    }

    suspend fun restoreFromEncryptedBytes(wrapper: ByteArray, keyId: String, merge: Boolean): Int {
        val plaintext = decryptWrapper(wrapper, keyId)
        try {
            return vaultService.importBackupJsonBytes(plaintext, merge = merge)
        } finally {
            plaintext.fill(0)
        }
    }

    suspend fun restoreFromStoredRecord(recordId: String, merge: Boolean): Int {
        val record = backupRegistryStore.getById(recordId)
            ?: throw IllegalArgumentException("Backup record not found")
        return restoreFromRemote(
            mediaId = record.downloadPathId,
            keyId = record.keyId,
            merge = merge,
            downloadToken = record.downloadToken,
            downloadExpires = record.downloadExpires
        )
    }

    fun listBackupRecords(): List<BackupRecord> = backupRegistryStore.list()

    fun deleteBackupRecord(recordId: String): Boolean = backupRegistryStore.delete(recordId)

    suspend fun buildEncryptedBackupWrapper(keyId: String): ByteArray {
        val keyMaterial = sharedKeyStore.getKeyMaterial(keyId)
            ?: throw IllegalArgumentException("Shared key not found")
        val backupKey = deriveBackupKey(keyMaterial)
        keyMaterial.wipe()

        val backupJson = vaultService.exportBackupJsonBytes()
        val encrypted = noteCrypto.encrypt(backupJson, backupKey)
        backupKey.wipe()
        backupJson.fill(0)

        val payloadB64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        encrypted.fill(0)

        val wrapper = JSONObject().apply {
            put("v", 1)
            put("kind", "backup")
            put("ext", NulvexFileTypes.BACKUP_EXT)
            put("alg", "xchacha20poly1305")
            put("key_id", keyId)
            put("payload", payloadB64)
        }
        return wrapper.toString().toByteArray(Charsets.UTF_8)
    }

    private fun decryptWrapper(wrapperBytes: ByteArray, keyId: String): ByteArray {
        val root = JSONObject(wrapperBytes.toString(Charsets.UTF_8))
        val payloadB64 = root.optString("payload", "")
        require(payloadB64.isNotBlank()) { "Invalid backup payload" }

        val encrypted = Base64.decode(payloadB64, Base64.DEFAULT)
        val keyMaterial = sharedKeyStore.getKeyMaterial(keyId)
            ?: throw IllegalArgumentException("Shared key not found")
        val backupKey = deriveBackupKey(keyMaterial)
        keyMaterial.wipe()
        return try {
            noteCrypto.decrypt(encrypted, backupKey)
        } finally {
            encrypted.fill(0)
            backupKey.wipe()
        }
    }

    private fun deriveBackupKey(keyMaterial: ByteArray): ByteArray {
        return Hkdf.deriveKey(
            ikm = keyMaterial,
            salt = "nulvex-backup-salt-v1".toByteArray(Charsets.UTF_8),
            info = "nulvex-backup-key-v1".toByteArray(Charsets.UTF_8),
            length = 32
        )
    }

    private fun sha256Hex(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(data)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
