package com.androidircx.nulvex.work

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidircx.nulvex.VaultServiceLocator
import com.androidircx.nulvex.data.SyncStateStore
import com.androidircx.nulvex.sync.SyncEngine
import com.androidircx.nulvex.sync.SyncRemoteOpApplier
import com.androidircx.nulvex.sync.SyncPreferences
import com.androidircx.nulvex.security.PanicWipeService

class SyncWorker(
    context: android.content.Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sessionManager = VaultServiceLocator.sessionManager()
        val session = sessionManager.getActive() ?: return Result.success()
        val profile = sessionManager.getActiveProfile()?.id ?: "real"
        val syncPrefs = VaultServiceLocator.syncPreferences()
        val token = syncPrefs.getAuthToken(profile) ?: return Result.success()

        val stateStore = SyncStateStore(session.database.syncStateDao())
        var panicTriggered = false
        val applier = SyncRemoteOpApplier(
            noteDao = session.database.noteDao(),
            onRemotePanicWipe = {
                SyncPreferences(applicationContext).clearAuthToken(profile)
                PanicWipeService(applicationContext, sessionManager).wipeAll()
                panicTriggered = true
            }
        )
        val engine = SyncEngine(
            api = VaultServiceLocator.syncApi(),
            stateStore = stateStore,
            localRevisionLookup = { entityId ->
                if (panicTriggered) null else session.database.noteDao().getById(entityId)?.updatedAt?.toString()
            },
            applyRemoteOp = { op ->
                applier.apply(op)
            }
        )

        return try {
            engine.runCycle(profile = profile, token = token)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
