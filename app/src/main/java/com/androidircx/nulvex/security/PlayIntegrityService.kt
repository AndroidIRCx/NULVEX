package com.androidircx.nulvex.security

import android.content.Context
import com.androidircx.nulvex.BuildConfig
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import java.security.MessageDigest

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

    companion object {
        fun sha256(input: String): String {
            return MessageDigest.getInstance("SHA-256")
                .digest(input.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
        }
    }
}
