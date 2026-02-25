package com.androidircx.nulvex.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object ReminderNotificationHelper {
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val channel = NotificationChannel(
            ReminderConstants.NOTIFICATION_CHANNEL_ID,
            "Note reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Nulvex reminder notifications"
        }
        manager.createNotificationChannel(channel)
    }
}
