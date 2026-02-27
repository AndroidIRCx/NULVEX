package com.androidircx.nulvex.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sync_outbox",
    indices = [
        Index(value = ["nextAttemptAt"]),
        Index(value = ["profile", "createdAt"])
    ]
)
data class SyncOutboxEntity(
    @PrimaryKey val opId: String,
    val deviceId: String,
    val profile: String,
    val entityType: String,
    val entityId: String,
    val opType: String,
    val baseRevision: String?,
    val envelopeCiphertext: ByteArray,
    val clientTs: Long,
    val createdAt: Long,
    val attemptCount: Int = 0,
    val nextAttemptAt: Long = 0L
)

@Entity(tableName = "sync_cursor")
data class SyncCursorEntity(
    @PrimaryKey val profile: String,
    val cursorToken: String,
    val updatedAt: Long
)

@Entity(
    tableName = "sync_conflicts",
    indices = [
        Index(value = ["profile", "entityId"]),
        Index(value = ["resolvedAt"])
    ]
)
data class SyncConflictEntity(
    @PrimaryKey val id: String,
    val profile: String,
    val entityId: String,
    val localRevision: String?,
    val remoteRevision: String?,
    val remoteOpId: String?,
    val resolutionPolicy: String,
    val createdAt: Long,
    val resolvedAt: Long? = null
)
