package com.androidircx.nulvex.security

import android.content.Context

class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("nulvex_app_settings", Context.MODE_PRIVATE)
    private val customLabelsKey = "custom_labels"
    private val reminderSchedulesKey = "reminder_schedules"
    private val pendingReminderActionKey = "pending_reminder_action"
    private val pendingReminderNoteIdKey = "pending_reminder_note_id"

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

    fun isDecoyBiometricEnabled(): Boolean = prefs.getBoolean("decoy_biometric_enabled", false)

    fun setDecoyBiometricEnabled(value: Boolean) {
        prefs.edit().putBoolean("decoy_biometric_enabled", value).apply()
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

    fun getLanguageTag(): String = prefs.getString("language_tag", "system") ?: "system"

    fun setLanguageTag(value: String) {
        prefs.edit().putString("language_tag", value).apply()
    }

    fun isPinScrambleEnabled(): Boolean = prefs.getBoolean("pin_scramble", false)

    fun setPinScrambleEnabled(value: Boolean) {
        prefs.edit().putBoolean("pin_scramble", value).apply()
    }

    fun isHidePinLengthEnabled(): Boolean = prefs.getBoolean("hide_pin_length", false)

    fun setHidePinLengthEnabled(value: Boolean) {
        prefs.edit().putBoolean("hide_pin_length", value).apply()
    }

    fun getCustomLabels(): List<String> {
        val stored = prefs.getStringSet(customLabelsKey, emptySet()) ?: emptySet()
        return stored.map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    fun addCustomLabel(label: String): List<String> {
        val trimmed = label.trim()
        if (trimmed.isBlank()) return getCustomLabels()
        val updated = (getCustomLabels() + trimmed).distinct().sorted()
        prefs.edit().putStringSet(customLabelsKey, updated.toSet()).apply()
        return updated
    }

    fun removeCustomLabel(label: String): List<String> {
        val updated = getCustomLabels().filterNot { it == label.trim() }.sorted()
        prefs.edit().putStringSet(customLabelsKey, updated.toSet()).apply()
        return updated
    }

    fun upsertReminderSchedule(noteId: String, triggerAt: Long) {
        if (noteId.isBlank() || triggerAt <= 0L) return
        val current = getReminderSchedules().toMutableMap()
        current[noteId] = triggerAt
        prefs.edit().putStringSet(reminderSchedulesKey, encodeReminderSchedules(current)).apply()
    }

    fun removeReminderSchedule(noteId: String) {
        if (noteId.isBlank()) return
        val current = getReminderSchedules().toMutableMap()
        current.remove(noteId)
        prefs.edit().putStringSet(reminderSchedulesKey, encodeReminderSchedules(current)).apply()
    }

    fun clearReminderSchedules() {
        prefs.edit().remove(reminderSchedulesKey).apply()
    }

    fun getReminderSchedules(): Map<String, Long> {
        val raw = prefs.getStringSet(reminderSchedulesKey, emptySet()) ?: emptySet()
        val result = linkedMapOf<String, Long>()
        raw.forEach { entry ->
            val idx = entry.indexOf("::")
            if (idx <= 0) return@forEach
            val noteId = entry.substring(0, idx)
            val trigger = entry.substring(idx + 2).toLongOrNull() ?: return@forEach
            if (noteId.isNotBlank() && trigger > 0L) {
                result[noteId] = trigger
            }
        }
        return result
    }

    fun setPendingReminderAction(action: String, noteId: String) {
        if (action.isBlank() || noteId.isBlank()) return
        prefs.edit()
            .putString(pendingReminderActionKey, action)
            .putString(pendingReminderNoteIdKey, noteId)
            .apply()
    }

    fun getPendingReminderAction(): Pair<String, String>? {
        val action = prefs.getString(pendingReminderActionKey, null)?.trim().orEmpty()
        val noteId = prefs.getString(pendingReminderNoteIdKey, null)?.trim().orEmpty()
        if (action.isBlank() || noteId.isBlank()) return null
        return action to noteId
    }

    fun clearPendingReminderAction() {
        prefs.edit()
            .remove(pendingReminderActionKey)
            .remove(pendingReminderNoteIdKey)
            .apply()
    }

    private fun encodeReminderSchedules(entries: Map<String, Long>): Set<String> {
        return entries.map { (noteId, triggerAt) -> "$noteId::$triggerAt" }.toSet()
    }
}
