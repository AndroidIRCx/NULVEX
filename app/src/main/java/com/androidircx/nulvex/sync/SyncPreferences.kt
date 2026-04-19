package com.androidircx.nulvex.sync

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

class SyncPreferences(context: Context) {
    private val appContext = context.applicationContext
    private val legacyPrefs = appContext.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
    private val securePrefs: SharedPreferences? = createSecurePrefs(appContext)
    private val prefs: SharedPreferences = securePrefs ?: legacyPrefs

    init {
        migrateLegacyPrefsIfNeeded()
    }

    fun getOrCreateDeviceId(): String {
        val current = prefs.getString("device_id", null)
        if (!current.isNullOrBlank()) return current
        val generated = UUID.randomUUID().toString()
        prefs.edit().putString("device_id", generated).apply()
        return generated
    }

    fun setAuthToken(profile: String, token: SyncAuthToken) {
        prefs.edit()
            .putString("access_token_$profile", token.accessToken)
            .putString("refresh_token_$profile", token.refreshToken)
            .putLong("expires_at_$profile", token.expiresAtEpochMillis)
            .putString("token_device_id_$profile", token.deviceId)
            .apply()
    }

    fun getAuthToken(profile: String): SyncAuthToken? {
        val access = prefs.getString("access_token_$profile", null)?.trim().orEmpty()
        if (access.isBlank()) return null
        val refresh = prefs.getString("refresh_token_$profile", null)
        val expires = prefs.getLong("expires_at_$profile", 0L)
        val deviceId = prefs.getString("token_device_id_$profile", null)
            ?.takeIf { it.isNotBlank() }
            ?: getOrCreateDeviceId()
        return SyncAuthToken(
            accessToken = access,
            refreshToken = refresh,
            expiresAtEpochMillis = expires,
            deviceId = deviceId
        )
    }

    fun clearAuthToken(profile: String) {
        prefs.edit()
            .remove("access_token_$profile")
            .remove("refresh_token_$profile")
            .remove("expires_at_$profile")
            .remove("token_device_id_$profile")
            .apply()
    }

    fun setLastSyncResult(timestampMillis: Long, conflictCount: Int) {
        prefs.edit()
            .putLong("last_sync_at", timestampMillis)
            .putInt("last_sync_conflicts", conflictCount)
            .apply()
    }

    fun getLastSyncAt(): Long = prefs.getLong("last_sync_at", 0L)

    fun getLastSyncConflictCount(): Int = prefs.getInt("last_sync_conflicts", 0)

    private fun createSecurePrefs(context: Context): SharedPreferences? {
        return runCatching {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                SECURE_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }.getOrNull()
    }

    private fun migrateLegacyPrefsIfNeeded() {
        val secure = securePrefs ?: return
        val legacyData = legacyPrefs.all
        if (legacyData.isEmpty()) return

        val editor = secure.edit()
        legacyData.forEach { (key, value) ->
            when (value) {
                is String -> editor.putString(key, value)
                is Long -> editor.putLong(key, value)
                is Int -> editor.putInt(key, value)
                is Float -> editor.putFloat(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Set<*> -> {
                    if (value.all { it is String }) {
                        editor.putStringSet(key, value.filterIsInstance<String>().toSet())
                    }
                }
            }
        }
        val copied = editor.commit()
        if (copied) {
            legacyPrefs.edit().clear().apply()
        }
    }

    companion object {
        private const val LEGACY_PREFS_NAME = "nulvex_sync_prefs"
        private const val SECURE_PREFS_NAME = "nulvex_sync_secure_prefs"
    }
}
