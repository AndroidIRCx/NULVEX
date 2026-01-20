package com.androidircx.nulvex.data

import com.androidircx.nulvex.crypto.NoteCrypto
import com.androidircx.nulvex.crypto.XChaCha20Poly1305NoteCrypto
import com.androidircx.nulvex.security.VaultProfile
import com.androidircx.nulvex.security.wipe

class VaultService(
    private val sessionManager: VaultSessionManager,
    private val noteCrypto: NoteCrypto = XChaCha20Poly1305NoteCrypto()
) {
    private fun requireSession(): VaultSession {
        return sessionManager.getActive()
            ?: throw IllegalStateException("Vault is locked")
    }

    suspend fun createNote(
        content: String,
        expiresAt: Long? = null,
        readOnce: Boolean = false
    ): String {
        val session = requireSession()
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        return repo.saveNote(content, session.noteKey, expiresAt, readOnce)
    }

    suspend fun unlock(pin: CharArray, profile: VaultProfile = VaultProfile.REAL) {
        val session = sessionManager.open(pin, profile)
        val sweeper = SelfDestructService(session.database)
        sweeper.sweepExpired()
        pin.wipe()
    }

    fun lock() {
        sessionManager.close()
    }

    suspend fun readNote(id: String): Note? {
        val session = requireSession()
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        return repo.getNoteById(id, session.noteKey)
    }

    suspend fun listNotes(): List<Note> {
        val session = requireSession()
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        return repo.listNotes(session.noteKey)
    }

    suspend fun deleteNote(id: String) {
        val session = requireSession()
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        repo.deleteNote(id)
    }

    suspend fun sweepExpired() {
        val session = requireSession()
        val sweeper = SelfDestructService(session.database)
        sweeper.sweepExpired()
    }
}
