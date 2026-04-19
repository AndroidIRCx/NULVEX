package com.androidircx.nulvex.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class SecurityEvent(
    val id: String,
    val type: String,
    val detail: String,
    val timestampMillis: Long
)

class SecurityEventStore(context: Context) {
    private val appContext = context.applicationContext
    private val prefs: SharedPreferences = createPrefs(appContext)

    fun record(type: String, detail: String = "") {
        runCatching {
            val events = loadRaw()
            val event = JSONObject().apply {
                put("id", UUID.randomUUID().toString())
                put("type", type)
                put("detail", detail)
                put("ts", System.currentTimeMillis())
            }
            // Prepend — newest first
            val updated = JSONArray()
            updated.put(event)
            for (i in 0 until minOf(events.length(), MAX_EVENTS - 1)) {
                updated.put(events.get(i))
            }
            prefs.edit().putString(KEY_EVENTS, updated.toString()).apply()
        }
    }

    fun listEvents(): List<SecurityEvent> {
        return runCatching {
            val arr = loadRaw()
            (0 until arr.length()).mapNotNull { i ->
                val obj = arr.optJSONObject(i) ?: return@mapNotNull null
                SecurityEvent(
                    id = obj.optString("id", ""),
                    type = obj.optString("type", ""),
                    detail = obj.optString("detail", ""),
                    timestampMillis = obj.optLong("ts", 0L)
                )
            }
        }.getOrElse { emptyList() }
    }

    private fun loadRaw(): JSONArray {
        val raw = prefs.getString(KEY_EVENTS, null) ?: return JSONArray()
        return runCatching { JSONArray(raw) }.getOrElse { JSONArray() }
    }

    private fun createPrefs(context: Context): SharedPreferences {
        return runCatching {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }.getOrElse {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    companion object {
        private const val PREFS_NAME = "nulvex_security_events"
        private const val KEY_EVENTS = "events"
        private const val MAX_EVENTS = 200

        const val EVENT_UNLOCK_SUCCESS = "unlock_success"
        const val EVENT_UNLOCK_FAIL = "unlock_fail"
        const val EVENT_LOCKOUT = "lockout"
        const val EVENT_PANIC_WIPE = "panic_wipe"
        const val EVENT_KEY_IMPORT = "key_import"
        const val EVENT_KEY_DELETE = "key_delete"
        const val EVENT_KEY_GENERATE = "key_generate"
        const val EVENT_SYNC_AUTH_CHANGE = "sync_auth_change"
        const val EVENT_KEY_ROTATION = "key_rotation"
        const val EVENT_BACKUP_EXPORT = "backup_export"
        const val EVENT_BACKUP_IMPORT = "backup_import"
    }
}
