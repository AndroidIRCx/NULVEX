package com.androidircx.nulvex.sync

import android.content.Context
import com.androidircx.nulvex.security.LegacyEncryptedPrefsBridge
import com.androidircx.nulvex.security.SecureTypedPreferences
import java.util.UUID

class SyncPreferences(context: Context) {
    private val appContext = context.applicationContext
    private val legacyPrefs = appContext.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
    private val securePrefs: SecureTypedPreferences? = SecureTypedPreferences.create(appContext, SECURE_PREFS_NAME)

    init {
        migrateLegacyPrefsIfNeeded()
        migrateLegacyEncryptedPrefsIfNeeded()
    }

    fun getOrCreateDeviceId(): String {
        val current = getString("device_id", null)
        if (!current.isNullOrBlank()) return current
        val generated = UUID.randomUUID().toString()
        putString("device_id", generated)
        return generated
    }

    fun setAuthToken(profile: String, token: SyncAuthToken) {
        putString("access_token_$profile", token.accessToken)
        putString("refresh_token_$profile", token.refreshToken)
        putLong("expires_at_$profile", token.expiresAtEpochMillis)
        putString("token_device_id_$profile", token.deviceId)
    }

    fun getAuthToken(profile: String): SyncAuthToken? {
        val access = getString("access_token_$profile", null)?.trim().orEmpty()
        if (access.isBlank()) return null
        val refresh = getString("refresh_token_$profile", null)
        val expires = getLong("expires_at_$profile", 0L)
        val deviceId = getString("token_device_id_$profile", null)
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
        remove(
            "access_token_$profile",
            "refresh_token_$profile",
            "expires_at_$profile",
            "token_device_id_$profile"
        )
    }

    fun setLastSyncResult(timestampMillis: Long, conflictCount: Int) {
        putLong("last_sync_at", timestampMillis)
        putInt("last_sync_conflicts", conflictCount)
    }

    fun getLastSyncAt(): Long = getLong("last_sync_at", 0L)

    fun getLastSyncConflictCount(): Int = getInt("last_sync_conflicts", 0)

    private fun getString(key: String, defaultValue: String?): String? {
        return securePrefs?.getString(key, defaultValue) ?: legacyPrefs.getString(key, defaultValue)
    }

    private fun getLong(key: String, defaultValue: Long): Long {
        return securePrefs?.getLong(key, defaultValue) ?: legacyPrefs.getLong(key, defaultValue)
    }

    private fun getInt(key: String, defaultValue: Int): Int {
        return securePrefs?.getInt(key, defaultValue) ?: legacyPrefs.getInt(key, defaultValue)
    }

    private fun putString(key: String, value: String?) {
        val secure = securePrefs
        if (secure != null) {
            secure.putString(key, value)
            return
        }
        legacyPrefs.edit().putString(key, value).apply()
    }

    private fun putLong(key: String, value: Long) {
        val secure = securePrefs
        if (secure != null) {
            secure.putLong(key, value)
            return
        }
        legacyPrefs.edit().putLong(key, value).apply()
    }

    private fun putInt(key: String, value: Int) {
        val secure = securePrefs
        if (secure != null) {
            secure.putInt(key, value)
            return
        }
        legacyPrefs.edit().putInt(key, value).apply()
    }

    private fun remove(vararg keys: String) {
        val secure = securePrefs
        if (secure != null) {
            secure.remove(*keys)
            return
        }
        val editor = legacyPrefs.edit()
        keys.forEach { key -> editor.remove(key) }
        editor.apply()
    }

    private fun migrateLegacyPrefsIfNeeded() {
        val secure = securePrefs ?: return
        val legacyData = legacyPrefs.all
        if (legacyData.isEmpty()) return

        val copied = secure.putAll(legacyData, overwriteExisting = false)
        if (copied) {
            legacyPrefs.edit().clear().apply()
        }
    }

    private fun migrateLegacyEncryptedPrefsIfNeeded() {
        val secure = securePrefs ?: return
        if (secure.getBoolean(KEY_LEGACY_ENCRYPTED_MIGRATED, false)) return

        val legacyEncrypted = LegacyEncryptedPrefsBridge.open(appContext, LEGACY_ENCRYPTED_PREFS_NAME)
        val migratedData = legacyEncrypted?.all.orEmpty()
        if (migratedData.isNotEmpty()) {
            secure.putAll(migratedData, overwriteExisting = false)
        }
        secure.putBoolean(KEY_LEGACY_ENCRYPTED_MIGRATED, true)
    }

    companion object {
        private const val LEGACY_PREFS_NAME = "nulvex_sync_prefs"
        private const val LEGACY_ENCRYPTED_PREFS_NAME = "nulvex_sync_secure_prefs"
        private const val SECURE_PREFS_NAME = "nulvex_sync_secure_prefs_v2"
        private const val KEY_LEGACY_ENCRYPTED_MIGRATED = "__legacy_esp_migrated_v2"
    }
}
