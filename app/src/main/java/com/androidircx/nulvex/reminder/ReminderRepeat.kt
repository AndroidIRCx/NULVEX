package com.androidircx.nulvex.reminder

import java.util.Calendar

/**
 * Computes the next occurrence of a repeating reminder. Advances by whole intervals
 * relative to [from] but keeps advancing until strictly after [now], so a reminder that
 * fired late (device off, app closed) jumps straight to the next future slot instead of
 * re-firing immediately for every missed interval.
 *
 * Returns null when [repeat] is not a recognised repeat unit (i.e. a one-shot reminder).
 */
object ReminderRepeat {
    fun next(from: Long, repeat: String?, now: Long = System.currentTimeMillis()): Long? {
        val field = when (repeat?.trim()?.lowercase()) {
            "daily" -> Calendar.DAY_OF_YEAR
            "weekly" -> Calendar.WEEK_OF_YEAR
            "monthly" -> Calendar.MONTH
            else -> return null
        }
        val cal = Calendar.getInstance().apply { timeInMillis = from }
        do {
            cal.add(field, 1)
        } while (cal.timeInMillis <= now)
        return cal.timeInMillis
    }
}
