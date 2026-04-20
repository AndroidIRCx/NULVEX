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

        val commonFlags = PendingIntent.FLAG_IMMUTABLE
        val baseRequestCode = abs(noteId.hashCode())

        val openIntent = PendingIntent.getActivity(
            context,
            baseRequestCode,
            Intent(context, MainActivity::class.java).apply {
                `package` = context.packageName
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
                putExtra(ReminderConstants.EXTRA_ACTION, ReminderConstants.ACTION_OPEN)
            },
            commonFlags
        )
        val snoozeIntent = PendingIntent.getActivity(
            context,
            baseRequestCode + 1,
            Intent(context, MainActivity::class.java).apply {
                `package` = context.packageName
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
                putExtra(ReminderConstants.EXTRA_ACTION, ReminderConstants.ACTION_SNOOZE)
            },
            commonFlags
        )
        val doneIntent = PendingIntent.getActivity(
            context,
            baseRequestCode + 2,
            Intent(context, MainActivity::class.java).apply {
                `package` = context.packageName
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
                putExtra(ReminderConstants.EXTRA_ACTION, ReminderConstants.ACTION_DONE)
            },
            commonFlags
        )

        val notification = NotificationCompat.Builder(context, ReminderConstants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.reminder_notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openIntent)
            .addAction(0, context.getString(R.string.reminder_action_open), openIntent)
            .addAction(0, context.getString(R.string.reminder_action_snooze_10m), snoozeIntent)
            .addAction(0, context.getString(R.string.reminder_action_mark_done), doneIntent)
            .build()

        NotificationManagerCompat.from(context).notify(abs(noteId.hashCode()), notification)
    }
}
