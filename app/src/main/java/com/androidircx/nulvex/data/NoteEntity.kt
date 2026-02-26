package com.androidircx.nulvex.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["expiresAt"]),
        Index(value = ["deleted"]),
        Index(value = ["archivedAt"]),
        Index(value = ["reminderAt"]),
        Index(value = ["trashedAt"])
    ]
)
data class NoteEntity(
    @PrimaryKey val id: String,
    val ciphertext: ByteArray,
    val createdAt: Long,
    val expiresAt: Long?,
    val readOnce: Boolean,
    val deleted: Boolean,
    val archivedAt: Long? = null,
    val reminderAt: Long? = null,
    val reminderDone: Boolean = false,
    val reminderRepeat: String? = null,
    val trashedAt: Long? = null,
    val updatedAt: Long = createdAt
)
