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

        val baseRequestCode = abs(noteId.hashCode())

        val openActionIntent = Intent()
        openActionIntent.setClass(context, MainActivity::class.java)
        openActionIntent.setPackage(context.packageName)
        openActionIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        openActionIntent.putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
        openActionIntent.putExtra(ReminderConstants.EXTRA_ACTION, ReminderConstants.ACTION_OPEN)
        val openIntent = PendingIntent.getActivity(
            context,
            baseRequestCode,
            openActionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeActionIntent = Intent()
        snoozeActionIntent.setClass(context, MainActivity::class.java)
        snoozeActionIntent.setPackage(context.packageName)
        snoozeActionIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        snoozeActionIntent.putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
        snoozeActionIntent.putExtra(ReminderConstants.EXTRA_ACTION, ReminderConstants.ACTION_SNOOZE)
        val snoozeIntent = PendingIntent.getActivity(
            context,
            baseRequestCode + 1,
            snoozeActionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val doneActionIntent = Intent()
        doneActionIntent.setClass(context, MainActivity::class.java)
        doneActionIntent.setPackage(context.packageName)
        doneActionIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        doneActionIntent.putExtra(ReminderConstants.EXTRA_NOTE_ID, noteId)
        doneActionIntent.putExtra(ReminderConstants.EXTRA_ACTION, ReminderConstants.ACTION_DONE)
        val doneIntent = PendingIntent.getActivity(
            context,
            baseRequestCode + 2,
            doneActionIntent,
            PendingIntent.FLAG_IMMUTABLE
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
