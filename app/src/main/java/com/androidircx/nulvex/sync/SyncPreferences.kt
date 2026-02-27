package com.androidircx.nulvex.sync

import android.content.Context
import java.util.UUID

class SyncPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("nulvex_sync_prefs", Context.MODE_PRIVATE)

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
}
