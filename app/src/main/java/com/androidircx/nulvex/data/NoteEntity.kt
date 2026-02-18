package com.androidircx.nulvex.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["expiresAt"]),
        Index(value = ["deleted"])
    ]
)
data class NoteEntity(
    @PrimaryKey val id: String,
    val ciphertext: ByteArray,
    val createdAt: Long,
    val expiresAt: Long?,
    val readOnce: Boolean,
    val deleted: Boolean
)
