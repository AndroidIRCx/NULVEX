package com.androidircx.nulvex.data

enum class ReminderRepeat(val wire: String) {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly");

    companion object {
        fun fromWire(value: String?): ReminderRepeat? {
            return entries.firstOrNull { it.wire == value?.trim()?.lowercase() }
        }
    }
}
