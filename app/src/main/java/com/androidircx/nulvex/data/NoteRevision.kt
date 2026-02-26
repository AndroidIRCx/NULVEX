package com.androidircx.nulvex.data

data class NoteRevision(
    val id: String,
    val noteId: String,
    val note: Note,
    val createdAt: Long
)

