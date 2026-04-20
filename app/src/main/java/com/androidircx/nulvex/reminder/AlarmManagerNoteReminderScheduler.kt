package com.androidircx.nulvex.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlin.math.abs

class AlarmManagerNoteReminderScheduler(
    private val context: Context
) : NoteReminderScheduler {
    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(request: ReminderRequest) {
        val triggerAt = request.triggerAtEpochMillis
        val noteId = request.noteId.trim()
        if (triggerAt <= 0L || noteId.isBlank()) return

        val intent = Intent(context, NoteReminderReceiver::class.java).apply {
            putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCodeFor(noteId),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } catch (_: SecurityException) {
            // On newer Android versions exact alarms can require explicit user-granted permission.
            runCatching {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        }
    }

    override fun cancel(noteId: String) {
        val normalizedNoteId = noteId.trim()
        if (normalizedNoteId.isBlank()) return

        val intent = Intent(context, NoteReminderReceiver::class.java).apply {
            putExtra(ReminderConstants.EXTRA_NOTE_ID, normalizedNoteId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCodeFor(normalizedNoteId),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun cancelAll() {
        // We cancel known reminders through AppPreferences-driven iteration.
    }

    private fun requestCodeFor(noteId: String): Int = abs(noteId.hashCode())
}
