package com.androidircx.nulvex

import android.app.Application
import android.content.Context
import android.os.UserManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.androidircx.nulvex.i18n.sanitizeLanguageTag
import com.androidircx.nulvex.i18n.tx
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.androidircx.nulvex.reminder.ReminderNotificationHelper
import com.androidircx.nulvex.reminder.ReminderRequest
import com.androidircx.nulvex.security.AppPreferences
import com.androidircx.nulvex.work.SelfDestructWorker
import com.androidircx.nulvex.work.SyncWorkScheduler
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.concurrent.TimeUnit

class NulvexApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val userManager = getSystemService(Context.USER_SERVICE) as UserManager
        val isUnlocked = userManager.isUserUnlocked
        if (isUnlocked) {
            applySavedLanguage()
        }
        VaultServiceLocator.init(this)
        if (isUnlocked) {
            MobileAds.initialize(this)
            FirebaseCrashlytics.getInstance().setCustomKey("app_locale", AppPreferences(this).getLanguageTag())
            reschedulePersistedReminders()
        }
        warmUpPlayIntegrity()
        scheduleSelfDestructWork()
        SyncWorkScheduler.schedule(this)
        ReminderNotificationHelper.ensureChannel(this)
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
                    title = tx("Note reminder"),
                    preview = ""
                )
            )
        }
    }

    private fun warmUpPlayIntegrity() {
        val playIntegrity = VaultServiceLocator.playIntegrityService()
        if (!playIntegrity.isConfigured()) return
        playIntegrity.prepare()
    }
}
