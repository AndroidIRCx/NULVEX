package com.androidircx.nulvex.sync

interface SyncApi {
    suspend fun registerDevice(
        profile: String,
        token: SyncAuthToken,
        requestSecurity: SyncRequestSecurity? = null
    ): Boolean

    suspend fun push(
        profile: String,
        token: SyncAuthToken,
        operations: List<SyncEnvelope>,
        requestSecurity: SyncRequestSecurity? = null
    ): List<SyncPushAck>

    suspend fun pull(
        profile: String,
        token: SyncAuthToken,
        cursorToken: String?,
        limit: Int,
        requestSecurity: SyncRequestSecurity? = null
    ): SyncPullResult
}
