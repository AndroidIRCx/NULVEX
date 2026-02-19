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

    fun hasSeenOnboarding(): Boolean = prefs.getBoolean("has_seen_onboarding", false)

    fun setHasSeenOnboarding(value: Boolean) {
        prefs.edit().putBoolean("has_seen_onboarding", value).apply()
    }

    fun getWrongAttempts(): Int = prefs.getInt("wrong_attempts", 0)

    fun setWrongAttempts(value: Int) {
        prefs.edit().putInt("wrong_attempts", value).apply()
    }

    fun getLockoutUntil(): Long = prefs.getLong("lockout_until", 0L)

    fun setLockoutUntil(value: Long) {
        prefs.edit().putLong("lockout_until", value).apply()
    }
}
