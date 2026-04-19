package com.androidircx.nulvex.work

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidircx.nulvex.VaultServiceLocator
import com.androidircx.nulvex.data.SyncStateStore
import com.androidircx.nulvex.sync.SyncEngine
import com.androidircx.nulvex.sync.SyncRemoteOpApplier
import com.androidircx.nulvex.sync.SyncPreferences
import com.androidircx.nulvex.sync.SyncRequestSecurity
import com.androidircx.nulvex.sync.SyncSecurityRequestContext
import com.androidircx.nulvex.security.PanicWipeService
import com.androidircx.nulvex.security.PlayIntegrityService

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
        val syncApi = VaultServiceLocator.syncApi()
        val engine = SyncEngine(
            api = syncApi,
            stateStore = stateStore,
            localRevisionLookup = { entityId ->
                if (panicTriggered) null else session.database.noteDao().getById(entityId)?.updatedAt?.toString()
            },
            applyRemoteOp = { op ->
                applier.apply(op)
            },
            requestSecurityProvider = { request ->
                buildRequestSecurity(
                    request = request,
                    playIntegrity = VaultServiceLocator.playIntegrityService()
                )
            }
        )

        return try {
            val registerSecurity = buildRequestSecurity(
                request = SyncSecurityRequestContext(
                    action = "register",
                    profile = profile,
                    deviceId = token.deviceId,
                    cursorToken = null,
                    operationsCount = 0
                ),
                playIntegrity = VaultServiceLocator.playIntegrityService()
            )
            val registered = syncApi.registerDevice(
                profile = profile,
                token = token,
                requestSecurity = registerSecurity
            )
            if (!registered) {
                return Result.retry()
            }
            val report = engine.runCycle(profile = profile, token = token)
            syncPrefs.setLastSyncResult(System.currentTimeMillis(), report.pulledConflicts)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private suspend fun buildRequestSecurity(
        request: SyncSecurityRequestContext,
        playIntegrity: PlayIntegrityService
    ): SyncRequestSecurity? {
        if (!playIntegrity.isConfigured()) return null

        val issuedAt = System.currentTimeMillis() / 1000L
        val hashInput = listOf(
            "sync-v1",
            request.action,
            request.profile,
            request.deviceId,
            request.cursorToken ?: "-",
            request.operationsCount.toString(),
            issuedAt.toString()
        ).joinToString("|")
        val requestHash = PlayIntegrityService.sha256(hashInput)
        val integrityToken = playIntegrity.requestTokenOrNull(requestHash) ?: return null

        return SyncRequestSecurity(
            integrityToken = integrityToken,
            requestHash = requestHash,
            issuedAtEpochSeconds = issuedAt
        )
    }
}
