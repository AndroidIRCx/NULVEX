package com.androidircx.nulvex.security

import android.content.Context

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("nulvex_app_settings", Context.MODE_PRIVATE)

    fun getLockTimeoutMs(): Long = prefs.getLong("lock_timeout_ms", 60_000L)

    fun setLockTimeoutMs(value: Long) {
        prefs.edit().putLong("lock_timeout_ms", value).apply()
    }

    fun getDefaultExpiry(): String = prefs.getString("default_expiry", "none") ?: "none"

    fun setDefaultExpiry(value: String) {
        prefs.edit().putString("default_expiry", value).apply()
    }

    fun getDefaultReadOnce(): Boolean = prefs.getBoolean("default_read_once", false)

    fun setDefaultReadOnce(value: Boolean) {
        prefs.edit().putBoolean("default_read_once", value).apply()
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean("biometric_enabled", false)

    fun setBiometricEnabled(value: Boolean) {
        prefs.edit().putBoolean("biometric_enabled", value).apply()
    }

    fun getThemeMode(): String = prefs.getString("theme_mode", "system") ?: "system"

    fun setThemeMode(value: String) {
        prefs.edit().putString("theme_mode", value).apply()
    }
}
