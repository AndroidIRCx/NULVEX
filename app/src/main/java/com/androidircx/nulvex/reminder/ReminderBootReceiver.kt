package com.androidircx.nulvex.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.androidircx.nulvex.security.AppPreferences

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (
            action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_TIME_CHANGED &&
            action != Intent.ACTION_TIMEZONE_CHANGED &&
            action != "android.intent.action.TIME_SET"
        ) {
            return
        }

        val prefs = AppPreferences(context)
        val scheduler = AlarmManagerNoteReminderScheduler(context)
        ReminderNotificationHelper.ensureChannel(context)
        prefs.getReminderSchedules().forEach { (noteId, triggerAt) ->
            scheduler.schedule(
                ReminderRequest(
                    noteId = noteId,
                    triggerAtEpochMillis = triggerAt,
                    title = "Reminder",
                    preview = ""
                )
            )
        }
    }
}
