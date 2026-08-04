package com.androidircx.nulvex.reminder

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderRepeatTest {

    @Test
    fun oneShotOrUnknownReturnsNull() {
        assertNull(ReminderRepeat.next(1_000L, null, now = 2_000L))
        assertNull(ReminderRepeat.next(1_000L, "", now = 2_000L))
        assertNull(ReminderRepeat.next(1_000L, "never", now = 2_000L))
    }

    @Test
    fun dailyJumpsPastNowForALateReminder() {
        val from = 0L
        val now = 3L * 24 * 3_600_000L + 123L // ~3 days after the original trigger
        val next = ReminderRepeat.next(from, "daily", now = now)
        assertNotNull(next)
        // Must be strictly in the future rather than re-firing once per missed day.
        assertTrue(next!! > now)
    }

    @Test
    fun weeklyAndMonthlyAdvanceAtLeastOneInterval() {
        val from = 1_000_000L
        val weekly = ReminderRepeat.next(from, "weekly", now = from)
        val monthly = ReminderRepeat.next(from, "monthly", now = from)
        assertNotNull(weekly)
        assertNotNull(monthly)
        assertTrue(weekly!! > from)
        assertTrue(monthly!! > from)
    }
}
