package com.androidircx.nulvex.data

import com.androidircx.nulvex.crypto.NoteCrypto
import java.util.UUID

class NoteRepository(
    private val noteDao: NoteDao,
    private val noteCrypto: NoteCrypto
) {
    suspend fun saveNote(
        content: String,
        noteKey: ByteArray,
        expiresAt: Long? = null,
        readOnce: Boolean = false
    ): String {
        val id = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()
        val plaintext = content.toByteArray(Charsets.UTF_8)
        val ciphertext = noteCrypto.encrypt(plaintext, noteKey)
        val entity = NoteEntity(
            id = id,
            ciphertext = ciphertext,
            createdAt = createdAt,
            expiresAt = expiresAt,
            readOnce = readOnce,
            deleted = false
        )
        noteDao.upsert(entity)
        return id
    }

    suspend fun getNoteById(id: String, noteKey: ByteArray): Note? {
        val entity = noteDao.getById(id) ?: return null
        if (entity.deleted) return null
        val plaintext = noteCrypto.decrypt(entity.ciphertext, noteKey)
        val content = plaintext.toString(Charsets.UTF_8)
        val note = Note(
            id = entity.id,
            content = content,
            createdAt = entity.createdAt,
            expiresAt = entity.expiresAt,
            readOnce = entity.readOnce
        )
        if (entity.readOnce) {
            destroyEntity(entity)
        }
        return note
    }

    suspend fun listNotes(noteKey: ByteArray): List<Note> {
        val entities = noteDao.listActive()
        return entities.mapNotNull { entity ->
            if (entity.deleted) return@mapNotNull null
            val plaintext = noteCrypto.decrypt(entity.ciphertext, noteKey)
            val content = plaintext.toString(Charsets.UTF_8)
            Note(
                id = entity.id,
                content = content,
                createdAt = entity.createdAt,
                expiresAt = entity.expiresAt,
                readOnce = entity.readOnce
            )
        }
    }

    suspend fun deleteNote(id: String) {
        val entity = noteDao.getById(id) ?: return
        destroyEntity(entity)
    }

    private suspend fun destroyEntity(entity: NoteEntity) {
        val zeroed = ByteArray(entity.ciphertext.size)
        noteDao.overwriteCiphertext(entity.id, zeroed)
        noteDao.softDelete(entity.id)
    }
}
