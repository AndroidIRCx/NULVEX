package com.androidircx.nulvex.sync

data class SyncAuthToken(
    val accessToken: String,
    val refreshToken: String?,
    val expiresAtEpochMillis: Long,
    val deviceId: String
)

data class SyncEnvelope(
    val opId: String,
    val profile: String,
    val entityType: String,
    val entityId: String,
    val opType: String,
    val baseRevision: String?,
    val clientTs: Long,
    val ciphertext: ByteArray
)

data class SyncPushAck(
    val opId: String,
    val accepted: Boolean
)

data class SyncPulledOp(
    val opId: String,
    val entityId: String,
    val revision: String?,
    val baseRevision: String?,
    val ciphertext: ByteArray
)

data class SyncPullResult(
    val cursorToken: String?,
    val operations: List<SyncPulledOp>
)
