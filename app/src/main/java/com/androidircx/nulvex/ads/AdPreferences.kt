package com.androidircx.nulvex.ads

import android.content.Context

/**
 * Stores ad-free expiry and share credits in private SharedPreferences.
 *
 * Tamper-resistance (ad-free timer):
 *  - Clock rollback detection: on each [isAdFree] call the current time is compared
 *    against [KEY_LAST_KNOWN_TIME]. If the device clock was rolled backward by more
 *    than [CLOCK_SKEW_TOLERANCE_MS] the timer is cleared immediately, preventing
 *    the user from extending ad-free time by manipulating the system clock.
 *  - [KEY_LAST_KNOWN_TIME] can only move forward.
 */
class AdPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // -------------------------------------------------------------------------
    // Ad-free timer
    // -------------------------------------------------------------------------

    /** Returns true if the current time is before the stored ad-free expiry. */
    fun isAdFree(): Boolean {
        if (hasRemoveAdsLifetime()) return true

        val now = System.currentTimeMillis()
        val lastKnown = prefs.getLong(KEY_LAST_KNOWN_TIME, 0L)

        // Clock was rolled back — treat as expired and clear
        if (lastKnown > 0L && now < lastKnown - CLOCK_SKEW_TOLERANCE_MS) {
            clearAdFree()
            return false
        }

        // Advance the watermark so future checks can detect a rollback
        if (now > lastKnown) {
            prefs.edit().putLong(KEY_LAST_KNOWN_TIME, now).apply()
        }

        val adFreeUntil = prefs.getLong(KEY_AD_FREE_UNTIL, 0L)
        return adFreeUntil > 0L && now < adFreeUntil
    }

    /**
     * Extends (or starts) the ad-free window by [durationMs] milliseconds.
     * If an existing window is still active it is pushed forward by [durationMs]
     * from its current expiry, so repeated watches stack properly.
     */
    fun extendAdFreeBy(durationMs: Long) {
        val now = System.currentTimeMillis()
        val currentExpiry = prefs.getLong(KEY_AD_FREE_UNTIL, 0L)
        // Stack on top of remaining time if still active; otherwise start fresh
        val base = if (currentExpiry > now) currentExpiry else now
        prefs.edit()
            .putLong(KEY_AD_FREE_UNTIL, base + durationMs)
            .putLong(KEY_LAST_KNOWN_TIME, now)
            .apply()
    }

    /** Returns the raw expiry epoch millis (0 if not set / expired). */
    fun getAdFreeUntil(): Long = prefs.getLong(KEY_AD_FREE_UNTIL, 0L)

    /** Clears the ad-free window (ads show immediately on next check). */
    fun clearAdFree() {
        prefs.edit().remove(KEY_AD_FREE_UNTIL).apply()
    }

    /** Enables permanent ad removal entitlement. */
    fun enableRemoveAdsLifetime() {
        prefs.edit().putBoolean(KEY_REMOVE_ADS_LIFETIME, true).apply()
    }

    fun hasRemoveAdsLifetime(): Boolean = prefs.getBoolean(KEY_REMOVE_ADS_LIFETIME, false)

    // -------------------------------------------------------------------------
    // Share credits
    // -------------------------------------------------------------------------

    /** Returns the current share credit balance. */
    fun getShareCredits(): Int = prefs.getInt(KEY_SHARE_CREDITS, 0)

    fun enableProFeaturesLifetime() {
        prefs.edit().putBoolean(KEY_PRO_FEATURES_LIFETIME, true).apply()
    }

    fun hasProFeaturesLifetime(): Boolean = prefs.getBoolean(KEY_PRO_FEATURES_LIFETIME, false)

    fun hasUnlimitedShares(): Boolean = hasProFeaturesLifetime()

    /** Adds [amount] share credits to the balance. */
    fun addShareCredits(amount: Int) {
        val current = prefs.getInt(KEY_SHARE_CREDITS, 0)
        prefs.edit().putInt(KEY_SHARE_CREDITS, current + amount).apply()
    }

    /**
     * Consumes [amount] share credits. Returns true if the balance was sufficient,
     * false if not enough credits (balance unchanged).
     */
    fun consumeShareCredits(amount: Int): Boolean {
        if (hasUnlimitedShares()) return true
        val current = prefs.getInt(KEY_SHARE_CREDITS, 0)
        if (current < amount) return false
        prefs.edit().putInt(KEY_SHARE_CREDITS, current - amount).apply()
        return true
    }

    companion object {
        private const val PREFS_NAME = "nulvex_ad_prefs"
        private const val KEY_AD_FREE_UNTIL = "ad_free_until"
        private const val KEY_REMOVE_ADS_LIFETIME = "remove_ads_lifetime"
        private const val KEY_LAST_KNOWN_TIME = "last_known_time"
        private const val KEY_SHARE_CREDITS = "share_credits"
        private const val KEY_PRO_FEATURES_LIFETIME = "pro_features_lifetime"

        /** Allow up to 5 seconds of natural clock drift between checks. */
        private const val CLOCK_SKEW_TOLERANCE_MS = 5_000L
    }
}
