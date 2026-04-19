package com.androidircx.nulvex.security

import android.content.Context
import com.androidircx.nulvex.BuildConfig
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.MessageDigest
import kotlin.coroutines.resume

class PlayIntegrityService(context: Context) {
    private val appContext = context.applicationContext
    private val manager = IntegrityManagerFactory.createStandard(appContext)

    @Volatile
    private var tokenProvider: StandardIntegrityManager.StandardIntegrityTokenProvider? = null

    fun isConfigured(): Boolean = BuildConfig.PLAY_INTEGRITY_CLOUD_PROJECT_NUMBER > 0L

    fun prepare(onComplete: (Result<Unit>) -> Unit = {}) {
        if (!isConfigured()) {
            onComplete(Result.failure(IllegalStateException("Play Integrity cloud project number is not configured")))
            return
        }

        manager.prepareIntegrityToken(
            StandardIntegrityManager.PrepareIntegrityTokenRequest.builder()
                .setCloudProjectNumber(BuildConfig.PLAY_INTEGRITY_CLOUD_PROJECT_NUMBER)
                .build()
        ).addOnSuccessListener { provider ->
            tokenProvider = provider
            onComplete(Result.success(Unit))
        }.addOnFailureListener { error ->
            onComplete(Result.failure(error))
        }
    }

    fun requestToken(requestHash: String, onComplete: (Result<String>) -> Unit) {
        val provider = tokenProvider
        if (provider == null) {
            onComplete(Result.failure(IllegalStateException("Play Integrity token provider is not prepared")))
            return
        }

        provider.request(
            StandardIntegrityManager.StandardIntegrityTokenRequest.builder()
                .setRequestHash(requestHash.take(500))
                .build()
        ).addOnSuccessListener { token ->
            onComplete(Result.success(token.token()))
        }.addOnFailureListener { error ->
            onComplete(Result.failure(error))
        }
    }

    suspend fun requestTokenOrNull(requestHash: String): String? {
        if (!isConfigured()) return null

        if (tokenProvider == null) {
            val prepared = prepareSuspend()
            if (prepared.isFailure) return null
        }
        return requestTokenSuspend(requestHash).getOrNull()
    }

    private suspend fun prepareSuspend(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            prepare { result ->
                if (continuation.isActive) continuation.resume(result)
            }
        }
    }

    private suspend fun requestTokenSuspend(requestHash: String): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            requestToken(requestHash) { result ->
                if (continuation.isActive) continuation.resume(result)
            }
        }
    }

    companion object {
        fun sha256(input: String): String {
            return MessageDigest.getInstance("SHA-256")
                .digest(input.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
        }
    }
}
