package com.androidircx.nulvex.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidircx.nulvex.VaultServiceLocator

class SelfDestructWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val session = VaultServiceLocator.sessionManager().getActive()
            ?: return Result.success()
        val vaultService = VaultServiceLocator.vaultService()
        return try {
            vaultService.sweepExpired()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
