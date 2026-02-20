package com.androidircx.nulvex.pro

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class BackupRecord(
    val id: String,
    val mediaId: String,
    val downloadPathId: String,
    val keyId: String,
    val downloadToken: String?,
    val downloadExpires: Long?,
    val sizeBytes: Int,
    val sha256: String,
    val createdAt: Long
)

class BackupRegistryStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun list(): List<BackupRecord> {
        val out = mutableListOf<BackupRecord>()
        val array = loadArray()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            out.add(
                BackupRecord(
                    id = obj.optString(KEY_ID),
                    mediaId = obj.optString(KEY_MEDIA_ID),
                    downloadPathId = obj.optString(KEY_DOWNLOAD_PATH_ID, obj.optString(KEY_MEDIA_ID)),
                    keyId = obj.optString(KEY_KEY_ID),
                    downloadToken = obj.optString(KEY_DOWNLOAD_TOKEN, "").ifBlank { null },
                    downloadExpires = obj.optLong(KEY_DOWNLOAD_EXPIRES).takeIf {
                        obj.has(KEY_DOWNLOAD_EXPIRES) && !obj.isNull(KEY_DOWNLOAD_EXPIRES)
                    },
                    sizeBytes = obj.optInt(KEY_SIZE_BYTES, 0),
                    sha256 = obj.optString(KEY_SHA256),
                    createdAt = obj.optLong(KEY_CREATED_AT, 0L)
                )
            )
        }
        return out.sortedByDescending { it.createdAt }
    }

    fun add(
        mediaId: String,
        downloadPathId: String,
        keyId: String,
        sizeBytes: Int,
        sha256: String,
        downloadToken: String?,
        downloadExpires: Long?
    ): BackupRecord {
        val now = System.currentTimeMillis()
        val record = BackupRecord(
            id = UUID.randomUUID().toString(),
            mediaId = mediaId,
            downloadPathId = downloadPathId,
            keyId = keyId,
            downloadToken = downloadToken,
            downloadExpires = downloadExpires,
            sizeBytes = sizeBytes,
            sha256 = sha256,
            createdAt = now
        )
        val array = loadArray()
        val obj = JSONObject().apply {
            put(KEY_ID, record.id)
            put(KEY_MEDIA_ID, record.mediaId)
            put(KEY_DOWNLOAD_PATH_ID, record.downloadPathId)
            put(KEY_KEY_ID, record.keyId)
            put(KEY_DOWNLOAD_TOKEN, record.downloadToken ?: JSONObject.NULL)
            put(KEY_DOWNLOAD_EXPIRES, record.downloadExpires ?: JSONObject.NULL)
            put(KEY_SIZE_BYTES, record.sizeBytes)
            put(KEY_SHA256, record.sha256)
            put(KEY_CREATED_AT, record.createdAt)
        }
        array.put(obj)
        saveArray(array)
        return record
    }

    fun getById(recordId: String): BackupRecord? {
        return list().firstOrNull { it.id == recordId }
    }

    fun delete(recordId: String): Boolean {
        val current = loadArray()
        val updated = JSONArray()
        var removed = false
        for (i in 0 until current.length()) {
            val obj = current.optJSONObject(i) ?: continue
            if (obj.optString(KEY_ID) == recordId) {
                removed = true
            } else {
                updated.put(obj)
            }
        }
        if (removed) saveArray(updated)
        return removed
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

    companion object {
        private const val PREFS_NAME = "nulvex_backup_registry"
        private const val KEY_ITEMS = "items"

        private const val KEY_ID = "id"
        private const val KEY_MEDIA_ID = "mediaId"
        private const val KEY_DOWNLOAD_PATH_ID = "downloadPathId"
        private const val KEY_KEY_ID = "keyId"
        private const val KEY_DOWNLOAD_TOKEN = "downloadToken"
        private const val KEY_DOWNLOAD_EXPIRES = "downloadExpires"
        private const val KEY_SIZE_BYTES = "sizeBytes"
        private const val KEY_SHA256 = "sha256"
        private const val KEY_CREATED_AT = "createdAt"
    }
}
