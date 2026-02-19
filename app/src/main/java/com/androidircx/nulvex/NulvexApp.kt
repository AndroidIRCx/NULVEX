package com.androidircx.nulvex

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.androidircx.nulvex.work.SelfDestructWorker
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.TimeUnit

class NulvexApp : Application() {
    override fun onCreate() {
        super.onCreate()
        VaultServiceLocator.init(this)
        MobileAds.initialize(this)
        scheduleSelfDestructWork()
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
}
