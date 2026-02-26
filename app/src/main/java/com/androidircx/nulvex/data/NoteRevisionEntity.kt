package com.androidircx.nulvex.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_revisions",
    indices = [
        Index(value = ["noteId", "createdAt"])
    ]
)
data class NoteRevisionEntity(
    @PrimaryKey val id: String,
    val noteId: String,
    val ciphertextSnapshot: ByteArray,
    val expiresAt: Long?,
    val readOnce: Boolean,
    val archivedAt: Long?,
    val reminderAt: Long?,
    val reminderDone: Boolean,
    val createdAt: Long
)

