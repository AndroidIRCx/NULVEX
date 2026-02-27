package com.androidircx.nulvex

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.androidircx.nulvex.i18n.sanitizeLanguageTag
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.androidircx.nulvex.reminder.ReminderNotificationHelper
import com.androidircx.nulvex.reminder.ReminderRequest
import com.androidircx.nulvex.security.AppPreferences
import com.androidircx.nulvex.work.SelfDestructWorker
import com.androidircx.nulvex.work.SyncWorkScheduler
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.TimeUnit

class NulvexApp : Application() {
    override fun onCreate() {
        super.onCreate()
        applySavedLanguage()
        VaultServiceLocator.init(this)
        MobileAds.initialize(this)
        scheduleSelfDestructWork()
        SyncWorkScheduler.schedule(this)
        ReminderNotificationHelper.ensureChannel(this)
        reschedulePersistedReminders()
    }

    private fun applySavedLanguage() {
        val requestedTag = AppPreferences(this).getLanguageTag()
        val locales = if (requestedTag == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(sanitizeLanguageTag(requestedTag))
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun scheduleSelfDestructWork() {
        val request = PeriodicWorkRequestBuilder<SelfDestructWorker>(12, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "nulvex_self_destruct_sweep",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun reschedulePersistedReminders() {
        val prefs = AppPreferences(this)
        val scheduler = VaultServiceLocator.noteReminderScheduler()
        prefs.getReminderSchedules().forEach { (noteId, triggerAt) ->
            scheduler.schedule(
                ReminderRequest(
                    noteId = noteId,
                    triggerAtEpochMillis = triggerAt,
                    title = "Note reminder",
                    preview = ""
                )
            )
        }
    }
}
