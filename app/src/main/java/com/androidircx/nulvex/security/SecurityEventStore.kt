package com.androidircx.nulvex.security

import android.content.Context
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
    private val securePrefs = SecureTypedPreferences.create(appContext, SECURE_PREFS_NAME)
    private val legacyPrefs = appContext.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)

    init {
        migrateLegacyDataIfNeeded()
    }

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
            writeRaw(updated.toString())
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
        val raw = readRaw() ?: return JSONArray()
        return runCatching { JSONArray(raw) }.getOrElse { JSONArray() }
    }

    private fun readRaw(): String? {
        return securePrefs?.getString(KEY_EVENTS, null) ?: legacyPrefs.getString(KEY_EVENTS, null)
    }

    private fun writeRaw(raw: String) {
        val secure = securePrefs
        if (secure != null) {
            secure.putString(KEY_EVENTS, raw)
            return
        }
        legacyPrefs.edit().putString(KEY_EVENTS, raw).apply()
    }

    private fun migrateLegacyDataIfNeeded() {
        val secure = securePrefs ?: return
        if (secure.getBoolean(KEY_MIGRATION_DONE, false)) return

        val legacyEncrypted = LegacyEncryptedPrefsBridge.open(appContext, LEGACY_PREFS_NAME)
        val migratedRaw = legacyEncrypted?.getString(KEY_EVENTS, null)
            ?: legacyPrefs.getString(KEY_EVENTS, null)
        if (!migratedRaw.isNullOrBlank()) {
            secure.putString(KEY_EVENTS, migratedRaw)
        }
        secure.putBoolean(KEY_MIGRATION_DONE, true)
    }

    companion object {
        private const val LEGACY_PREFS_NAME = "nulvex_security_events"
        private const val SECURE_PREFS_NAME = "nulvex_security_events_v2"
        private const val KEY_MIGRATION_DONE = "__migrated_v2"
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
