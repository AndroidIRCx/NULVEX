package com.androidircx.nulvex.sync

interface SyncApi {
    suspend fun registerDevice(profile: String, token: SyncAuthToken): Boolean
    suspend fun push(profile: String, token: SyncAuthToken, operations: List<SyncEnvelope>): List<SyncPushAck>
    suspend fun pull(profile: String, token: SyncAuthToken, cursorToken: String?, limit: Int): SyncPullResult
}
