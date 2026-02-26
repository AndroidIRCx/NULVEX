package com.androidircx.nulvex.quickcapture

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.androidircx.nulvex.MainActivity
import com.androidircx.nulvex.R

class NulvexQuickCaptureWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = buildViews(context)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        refreshAll(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            refreshAll(context)
        }
    }

    private fun refreshAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, NulvexQuickCaptureWidgetProvider::class.java)
        val ids = manager.getAppWidgetIds(component)
        onUpdate(context, manager, ids)
    }

    private fun buildViews(context: Context): RemoteViews {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            action = MainActivity.ACTION_QUICK_CAPTURE
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            9011,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return RemoteViews(context.packageName, R.layout.widget_quick_capture).apply {
            setOnClickPendingIntent(R.id.quick_capture_root, pendingIntent)
            setOnClickPendingIntent(R.id.quick_capture_button, pendingIntent)
        }
    }
}
