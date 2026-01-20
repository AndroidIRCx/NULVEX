package com.androidircx.nulvex.data

data class Note(
    val id: String,
    val content: String,
    val createdAt: Long,
    val expiresAt: Long?,
    val readOnce: Boolean
)
