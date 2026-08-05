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

    fun isAutoBiometricPromptEnabled(): Boolean = prefs.getBoolean("auto_biometric_prompt_enabled", true)

    fun setAutoBiometricPromptEnabled(value: Boolean) {
        prefs.edit().putBoolean("auto_biometric_prompt_enabled", value).apply()
    }

    fun isDecoyBiometricEnabled(): Boolean = prefs.getBoolean("decoy_biometric_enabled", false)

    fun setDecoyBiometricEnabled(value: Boolean) {
        prefs.edit().putBoolean("decoy_biometric_enabled", value).apply()
    }

    fun getBiometricTargetVault(): String = prefs.getString("biometric_target_vault", "real") ?: "real"

    fun setBiometricTargetVault(vault: String) {
        prefs.edit().putString("biometric_target_vault", vault).apply()
    }

    fun getThemeMode(): String = prefs.getString("theme_mode", "system") ?: "system"

    fun setThemeMode(value: String) {
        prefs.edit().putString("theme_mode", value).apply()
    }

    fun getThemePaletteId(): String = prefs.getString("theme_palette_id", "vault") ?: "vault"

    fun setThemePaletteId(value: String) {
        prefs.edit().putString("theme_palette_id", value).apply()
    }

    fun isDynamicColor(): Boolean = prefs.getBoolean("dynamic_color", false)

    fun setDynamicColor(value: Boolean) {
        prefs.edit().putBoolean("dynamic_color", value).apply()
    }

    fun getCustomThemesJson(): String = prefs.getString("custom_themes_json", "[]") ?: "[]"

    fun setCustomThemesJson(value: String) {
        prefs.edit().putString("custom_themes_json", value).apply()
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

    fun addCustomLabel(label: String): List<String> = synchronized(LOCK) {
        val trimmed = label.trim()
        if (trimmed.isBlank()) return getCustomLabels()
        val updated = (getCustomLabels() + trimmed).distinct().sorted()
        prefs.edit().putStringSet(customLabelsKey, updated.toSet()).apply()
        return updated
    }

    fun removeCustomLabel(label: String): List<String> = synchronized(LOCK) {
        val updated = getCustomLabels().filterNot { it == label.trim() }.sorted()
        prefs.edit().putStringSet(customLabelsKey, updated.toSet()).apply()
        return updated
    }

    fun upsertReminderSchedule(noteId: String, triggerAt: Long, repeat: String? = null) {
        if (noteId.isBlank() || triggerAt <= 0L) return
        synchronized(LOCK) {
            val current = getReminderScheduleEntries().toMutableMap()
            current[noteId] = ReminderSchedule(triggerAt, repeat?.trim()?.takeIf { it.isNotBlank() })
            prefs.edit().putStringSet(reminderSchedulesKey, encodeReminderSchedules(current)).apply()
        }
    }

    fun removeReminderSchedule(noteId: String) {
        if (noteId.isBlank()) return
        synchronized(LOCK) {
            val current = getReminderScheduleEntries().toMutableMap()
            current.remove(noteId)
            prefs.edit().putStringSet(reminderSchedulesKey, encodeReminderSchedules(current)).apply()
        }
    }

    fun clearReminderSchedules() {
        prefs.edit().remove(reminderSchedulesKey).apply()
    }

    fun getReminderSchedules(): Map<String, Long> {
        return getReminderScheduleEntries().mapValues { it.value.triggerAt }
    }

    /** The repeat unit persisted alongside a scheduled reminder, if any. */
    fun getReminderRepeat(noteId: String): String? {
        return getReminderScheduleEntries()[noteId]?.repeat
    }

    fun getReminderScheduleEntries(): Map<String, ReminderSchedule> {
        val raw = prefs.getStringSet(reminderSchedulesKey, emptySet()) ?: emptySet()
        val result = linkedMapOf<String, ReminderSchedule>()
        raw.forEach { entry ->
            // Format: "noteId::triggerAt" (legacy) or "noteId::triggerAt::repeat".
            val parts = entry.split("::")
            if (parts.size < 2) return@forEach
            val noteId = parts[0]
            val trigger = parts[1].toLongOrNull() ?: return@forEach
            val repeat = parts.getOrNull(2)?.takeIf { it.isNotBlank() }
            if (noteId.isNotBlank() && trigger > 0L) {
                result[noteId] = ReminderSchedule(trigger, repeat)
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

    companion object {
        // Serializes read-modify-write on the shared label / reminder-schedule sets across
        // AppPreferences instances (it is constructed in many places but backs one file).
        private val LOCK = Any()
    }

    private fun encodeReminderSchedules(entries: Map<String, ReminderSchedule>): Set<String> {
        return entries.map { (noteId, schedule) ->
            if (schedule.repeat.isNullOrBlank()) {
                "$noteId::${schedule.triggerAt}"
            } else {
                "$noteId::${schedule.triggerAt}::${schedule.repeat}"
            }
        }.toSet()
    }
}

data class ReminderSchedule(val triggerAt: Long, val repeat: String?)
