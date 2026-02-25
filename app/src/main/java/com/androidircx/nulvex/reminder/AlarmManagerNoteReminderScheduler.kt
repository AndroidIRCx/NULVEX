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
        if (triggerAt <= 0L) return
        val pendingIntent = buildReminderPendingIntent(context, request.noteId, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    override fun cancel(noteId: String) {
        val pendingIntent = buildReminderPendingIntent(context, noteId, PendingIntent.FLAG_NO_CREATE) ?: return
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun cancelAll() {
        // We cancel known reminders through AppPreferences-driven iteration.
    }

    private fun buildReminderPendingIntent(
        context: Context,
        noteId: String,
        flag: Int
    ): PendingIntent? {
        if (noteId.isBlank()) return null
        val intent = Intent(context, NoteReminderReceiver::class.java).apply {
            action = ReminderConstants.ACTION_FIRE
            putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCodeFor(noteId),
            intent,
            flag or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun requestCodeFor(noteId: String): Int = abs(noteId.hashCode())
}
