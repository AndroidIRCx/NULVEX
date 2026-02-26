package com.androidircx.nulvex.data

data class ChecklistItem(
    val id: String,
    val text: String,
    val checked: Boolean
)

data class NoteAttachment(
    val id: String,
    val name: String,
    val mimeType: String,
    val byteCount: Long
)

data class Note(
    val id: String,
    val text: String,
    val checklist: List<ChecklistItem>,
    val labels: List<String>,
    val attachments: List<NoteAttachment>,
    val pinned: Boolean,
    val createdAt: Long,
    val updatedAt: Long = createdAt,
    val expiresAt: Long?,
    val readOnce: Boolean,
    val archivedAt: Long? = null,
    val reminderAt: Long? = null,
    val reminderDone: Boolean = false,
    val reminderRepeat: String? = null,
    val trashedAt: Long? = null
) {
    fun matchesQuery(query: String): Boolean {
        if (query.isBlank()) return true
        val lowered = query.trim().lowercase()
        if (text.lowercase().contains(lowered)) return true
        if (labels.any { it.lowercase().contains(lowered) }) return true
        if (attachments.any { it.name.lowercase().contains(lowered) }) return true
        return checklist.any { it.text.lowercase().contains(lowered) }
    }
}
