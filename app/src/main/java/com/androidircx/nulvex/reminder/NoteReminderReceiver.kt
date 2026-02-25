package com.androidircx.nulvex.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.androidircx.nulvex.MainActivity
import com.androidircx.nulvex.R
import kotlin.math.abs

class NoteReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val noteId = intent?.getStringExtra(ReminderConstants.EXTRA_NOTE_ID)?.trim().orEmpty()
        if (noteId.isBlank()) return
        ReminderNotificationHelper.ensureChannel(context)

        val openIntent = reminderActionIntent(context, noteId, ReminderConstants.ACTION_OPEN, requestCodeOffset = 0)
        val snoozeIntent = reminderActionIntent(context, noteId, ReminderConstants.ACTION_SNOOZE, requestCodeOffset = 1)
        val doneIntent = reminderActionIntent(context, noteId, ReminderConstants.ACTION_DONE, requestCodeOffset = 2)

        val notification = NotificationCompat.Builder(context, ReminderConstants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Reminder for a secure note")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openIntent)
            .addAction(0, "Open", openIntent)
            .addAction(0, "Snooze 10m", snoozeIntent)
            .addAction(0, "Mark done", doneIntent)
            .build()

        NotificationManagerCompat.from(context).notify(abs(noteId.hashCode()), notification)
    }

    private fun reminderActionIntent(
        context: Context,
        noteId: String,
        action: String,
        requestCodeOffset: Int
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
            putExtra(ReminderConstants.EXTRA_ACTION, action)
        }
        return PendingIntent.getActivity(
            context,
            abs(noteId.hashCode() + requestCodeOffset),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
