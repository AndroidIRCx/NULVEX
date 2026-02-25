package com.androidircx.nulvex.reminder

/**
 * Scheduler abstraction for note reminders.
 * Real implementation can use AlarmManager/WorkManager while tests use an in-memory fake.
 */
interface NoteReminderScheduler {
    fun schedule(request: ReminderRequest)
    fun cancel(noteId: String)
    fun cancelAll()
}

data class ReminderRequest(
    val noteId: String,
    val triggerAtEpochMillis: Long,
    val title: String,
    val preview: String
)
