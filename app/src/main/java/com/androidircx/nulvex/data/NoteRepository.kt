package com.androidircx.nulvex.data

import com.androidircx.nulvex.crypto.NoteCrypto
import java.util.UUID

class NoteRepository(
    private val noteDao: NoteDao,
    private val noteCrypto: NoteCrypto
) {
    private companion object {
        const val MAX_REVISIONS_PER_NOTE = 20
        const val DEFAULT_TRASH_RETENTION_DAYS = 7L
    }

    suspend fun saveNote(
        id: String = UUID.randomUUID().toString(),
        text: String,
        checklist: List<ChecklistItem> = emptyList(),
        labels: List<String> = emptyList(),
        attachments: List<NoteAttachment> = emptyList(),
        pinned: Boolean = false,
        noteKey: ByteArray,
        expiresAt: Long? = null,
        readOnce: Boolean = false,
        reminderAt: Long? = null,
        reminderDone: Boolean = false,
        reminderRepeat: String? = null
    ): String {
        val createdAt = System.currentTimeMillis()
        val payload = NotePayload(
            text = text,
            checklist = checklist,
            labels = labels,
            attachments = attachments,
            pinned = pinned
        )
        val plaintext = NotePayloadCodec.encode(payload).toByteArray(Charsets.UTF_8)
        val ciphertext = noteCrypto.encrypt(plaintext, noteKey)
        val entity = NoteEntity(
            id = id,
            ciphertext = ciphertext,
            createdAt = createdAt,
            updatedAt = createdAt,
            expiresAt = expiresAt,
            readOnce = readOnce,
            deleted = false,
            reminderAt = reminderAt,
            reminderDone = reminderDone,
            reminderRepeat = reminderRepeat,
            trashedAt = null
        )
        noteDao.upsert(entity)
        return id
    }

    suspend fun getNoteById(id: String, noteKey: ByteArray): Note? {
        val entity = noteDao.getById(id) ?: return null
        if (entity.deleted) return null
        return decodeEntity(entity, noteKey)
    }

    suspend fun listNotes(noteKey: ByteArray): List<Note> {
        return listNotes(noteKey = noteKey, archived = false)
    }

    suspend fun listNotes(noteKey: ByteArray, archived: Boolean): List<Note> {
        val entities = if (archived) noteDao.listArchived() else noteDao.listActive()
        return entities.mapNotNull { entity ->
            if (entity.deleted) return@mapNotNull null
            decodeEntity(entity, noteKey)
        }
    }

    suspend fun listTrashedNotes(noteKey: ByteArray): List<Note> {
        return noteDao.listTrashed().mapNotNull { entity ->
            if (entity.deleted) return@mapNotNull null
            decodeEntity(entity, noteKey)
        }
    }

    suspend fun updateNote(note: Note, noteKey: ByteArray): Boolean {
        val entity = noteDao.getById(note.id) ?: return false
        if (entity.deleted) return false
        noteDao.insertRevision(
            NoteRevisionEntity(
                id = UUID.randomUUID().toString(),
                noteId = entity.id,
                ciphertextSnapshot = entity.ciphertext,
                expiresAt = entity.expiresAt,
                readOnce = entity.readOnce,
                archivedAt = entity.archivedAt,
                reminderAt = entity.reminderAt,
                reminderDone = entity.reminderDone,
                createdAt = System.currentTimeMillis()
            )
        )
        noteDao.pruneRevisions(entity.id, MAX_REVISIONS_PER_NOTE)
        val payload = NotePayload(
            text = note.text,
            checklist = note.checklist,
            labels = note.labels,
            attachments = note.attachments,
            pinned = note.pinned
        )
        val plaintext = NotePayloadCodec.encode(payload).toByteArray(Charsets.UTF_8)
        val ciphertext = noteCrypto.encrypt(plaintext, noteKey)
        noteDao.overwriteCiphertext(entity.id, ciphertext)
        noteDao.setUpdatedAt(entity.id, System.currentTimeMillis())
        return true
    }

    suspend fun listRevisions(noteId: String, noteKey: ByteArray, limit: Int = MAX_REVISIONS_PER_NOTE): List<NoteRevision> {
        if (noteId.isBlank() || limit <= 0) return emptyList()
        return noteDao.listRevisions(noteId, limit).mapNotNull { entity ->
            val note = decodeRevisionEntity(entity, noteKey) ?: return@mapNotNull null
            NoteRevision(
                id = entity.id,
                noteId = entity.noteId,
                note = note,
                createdAt = entity.createdAt
            )
        }
    }

    suspend fun restoreRevision(noteId: String, revisionId: String, noteKey: ByteArray): Boolean {
        if (noteId.isBlank() || revisionId.isBlank()) return false
        val current = noteDao.getById(noteId) ?: return false
        if (current.deleted) return false
        val revision = noteDao.getRevisionById(noteId, revisionId) ?: return false
        noteDao.insertRevision(
            NoteRevisionEntity(
                id = UUID.randomUUID().toString(),
                noteId = current.id,
                ciphertextSnapshot = current.ciphertext,
                expiresAt = current.expiresAt,
                readOnce = current.readOnce,
                archivedAt = current.archivedAt,
                reminderAt = current.reminderAt,
                reminderDone = current.reminderDone,
                createdAt = System.currentTimeMillis()
            )
        )
        noteDao.pruneRevisions(current.id, MAX_REVISIONS_PER_NOTE)
        noteDao.restoreFromRevision(
            id = noteId,
            ciphertext = revision.ciphertextSnapshot,
            expiresAt = revision.expiresAt,
            readOnce = revision.readOnce,
            archivedAt = revision.archivedAt,
            reminderAt = revision.reminderAt,
            reminderDone = revision.reminderDone
        )
        noteDao.setUpdatedAt(noteId, System.currentTimeMillis())
        return decodeRevisionEntity(revision, noteKey) != null
    }

    suspend fun deleteNote(id: String) {
        val entity = noteDao.getById(id) ?: return
        destroyEntity(entity)
    }

    suspend fun moveNoteToTrash(id: String): Boolean {
        val entity = noteDao.getById(id) ?: return false
        if (entity.deleted || entity.trashedAt != null) return false
        noteDao.setTrashedAt(id, System.currentTimeMillis())
        noteDao.setUpdatedAt(id, System.currentTimeMillis())
        return true
    }

    suspend fun restoreFromTrash(id: String): Boolean {
        val entity = noteDao.getById(id) ?: return false
        if (entity.deleted || entity.trashedAt == null) return false
        noteDao.setTrashedAt(id, null)
        noteDao.setUpdatedAt(id, System.currentTimeMillis())
        return true
    }

    suspend fun purgeOldTrash(
        now: Long = System.currentTimeMillis(),
        retentionDays: Long = DEFAULT_TRASH_RETENTION_DAYS
    ): Int {
        val retentionMs = retentionDays * 24L * 60L * 60L * 1000L
        val cutoff = now - retentionMs
        val stale = noteDao.listTrashedBefore(cutoff)
        stale.forEach { destroyEntity(it) }
        return noteDao.purgeDeleted()
    }

    suspend fun setArchived(id: String, archived: Boolean): Boolean {
        val entity = noteDao.getById(id) ?: return false
        if (entity.deleted || entity.trashedAt != null) return false
        val archivedAt = if (archived) System.currentTimeMillis() else null
        noteDao.setArchivedAt(id, archivedAt)
        noteDao.setUpdatedAt(id, System.currentTimeMillis())
        return true
    }

    suspend fun setReminder(id: String, reminderAt: Long?, reminderDone: Boolean, reminderRepeat: String?): Boolean {
        val entity = noteDao.getById(id) ?: return false
        if (entity.deleted || entity.trashedAt != null) return false
        noteDao.setReminder(id, reminderAt, reminderDone, reminderRepeat)
        noteDao.setUpdatedAt(id, System.currentTimeMillis())
        return true
    }

    private suspend fun destroyEntity(entity: NoteEntity) {
        val zeroed = ByteArray(entity.ciphertext.size)
        noteDao.overwriteCiphertext(entity.id, zeroed)
        noteDao.softDelete(entity.id)
    }

    private fun decodeEntity(entity: NoteEntity, noteKey: ByteArray): Note? {
        val plaintext = noteCrypto.decrypt(entity.ciphertext, noteKey)
        val raw = plaintext.toString(Charsets.UTF_8)
        val payload = NotePayloadCodec.decode(raw) ?: NotePayload(
            text = raw,
            checklist = emptyList(),
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false
        )
        return Note(
            id = entity.id,
            text = payload.text,
            checklist = payload.checklist,
            labels = payload.labels,
            attachments = payload.attachments,
            pinned = payload.pinned,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            expiresAt = entity.expiresAt,
            readOnce = entity.readOnce,
            archivedAt = entity.archivedAt,
            reminderAt = entity.reminderAt,
            reminderDone = entity.reminderDone,
            reminderRepeat = entity.reminderRepeat,
            trashedAt = entity.trashedAt
        )
    }

    private fun decodeRevisionEntity(entity: NoteRevisionEntity, noteKey: ByteArray): Note? {
        val plaintext = noteCrypto.decrypt(entity.ciphertextSnapshot, noteKey)
        val raw = plaintext.toString(Charsets.UTF_8)
        val payload = NotePayloadCodec.decode(raw) ?: NotePayload(
            text = raw,
            checklist = emptyList(),
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false
        )
        return Note(
            id = entity.noteId,
            text = payload.text,
            checklist = payload.checklist,
            labels = payload.labels,
            attachments = payload.attachments,
            pinned = payload.pinned,
            createdAt = entity.createdAt,
            updatedAt = entity.createdAt,
            expiresAt = entity.expiresAt,
            readOnce = entity.readOnce,
            archivedAt = entity.archivedAt,
            reminderAt = entity.reminderAt,
            reminderDone = entity.reminderDone,
            reminderRepeat = null,
            trashedAt = null
        )
    }
}
