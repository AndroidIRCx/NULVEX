package com.androidircx.nulvex.data

import com.androidircx.nulvex.crypto.NoteCrypto
import java.util.UUID

class NoteRepository(
    private val noteDao: NoteDao,
    private val noteCrypto: NoteCrypto
) {
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
        reminderDone: Boolean = false
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
            expiresAt = expiresAt,
            readOnce = readOnce,
            deleted = false,
            reminderAt = reminderAt,
            reminderDone = reminderDone
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

    suspend fun updateNote(note: Note, noteKey: ByteArray): Boolean {
        val entity = noteDao.getById(note.id) ?: return false
        if (entity.deleted) return false
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
        return true
    }

    suspend fun deleteNote(id: String) {
        val entity = noteDao.getById(id) ?: return
        destroyEntity(entity)
    }

    suspend fun setArchived(id: String, archived: Boolean): Boolean {
        val entity = noteDao.getById(id) ?: return false
        if (entity.deleted) return false
        val archivedAt = if (archived) System.currentTimeMillis() else null
        noteDao.setArchivedAt(id, archivedAt)
        return true
    }

    suspend fun setReminder(id: String, reminderAt: Long?, reminderDone: Boolean): Boolean {
        val entity = noteDao.getById(id) ?: return false
        if (entity.deleted) return false
        noteDao.setReminder(id, reminderAt, reminderDone)
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
            expiresAt = entity.expiresAt,
            readOnce = entity.readOnce,
            archivedAt = entity.archivedAt,
            reminderAt = entity.reminderAt,
            reminderDone = entity.reminderDone
        )
    }
}
