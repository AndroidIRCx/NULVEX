package com.androidircx.nulvex.data

import com.androidircx.nulvex.crypto.NoteCrypto
import com.androidircx.nulvex.crypto.XChaCha20Poly1305NoteCrypto
import com.androidircx.nulvex.security.VaultKeyManager
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
        sweeper.sweepExpired(vacuum = false)
        pin.wipe()
    }

    suspend fun unlockWithMasterKey(masterKey: ByteArray, profile: VaultProfile = VaultProfile.REAL) {
        val session = sessionManager.openWithMasterKey(masterKey, profile)
        val sweeper = SelfDestructService(session.database)
        sweeper.sweepExpired(vacuum = false)
        masterKey.wipe()
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

    suspend fun sweepExpired(vacuum: Boolean = false) {
        val session = requireSession()
        val sweeper = SelfDestructService(session.database)
        sweeper.sweepExpired(vacuum = vacuum)
    }

    suspend fun changeRealPin(oldPin: CharArray, newPin: CharArray) {
        val session = sessionManager.open(oldPin, VaultProfile.REAL)
        val keyManager = VaultKeyManager(sessionManager.context, VaultProfile.REAL)
        val newMasterKey = keyManager.deriveMasterKey(newPin)
        val newDbKey = keyManager.deriveDbKey(newMasterKey)
        val newNoteKey = keyManager.deriveNoteKey(newMasterKey)

        val noteDao = session.database.noteDao()
        val entities = noteDao.listActive()
        for (entity in entities) {
            val plaintext = noteCrypto.decrypt(entity.ciphertext, session.noteKey)
            val ciphertext = noteCrypto.encrypt(plaintext, newNoteKey)
            plaintext.fill(0)
            noteDao.overwriteCiphertext(entity.id, ciphertext)
        }

        val db = session.database.openHelper.writableDatabase
        db.execSQL("PRAGMA rekey = \"x'${newDbKey.toHex()}'\"")
        sessionManager.close()
        newMasterKey.wipe()
        newDbKey.wipe()
        newNoteKey.wipe()
        oldPin.wipe()
    }

    private fun ByteArray.toHex(): String {
        val result = StringBuilder(size * 2)
        for (b in this) {
            result.append(String.format("%02x", b))
        }
        return result.toString()
    }
}
