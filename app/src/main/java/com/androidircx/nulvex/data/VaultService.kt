package com.androidircx.nulvex.data

import android.net.Uri
import android.provider.OpenableColumns
import com.androidircx.nulvex.crypto.NoteCrypto
import com.androidircx.nulvex.crypto.XChaCha20Poly1305NoteCrypto
import com.androidircx.nulvex.security.VaultKeyManager
import com.androidircx.nulvex.security.VaultProfile
import com.androidircx.nulvex.security.wipe
import java.util.UUID

class VaultService(
    private val sessionManager: VaultSessionManager,
    private val noteCrypto: NoteCrypto = XChaCha20Poly1305NoteCrypto()
) {
    private val attachmentStore = NoteAttachmentStore(sessionManager.context)

    private fun requireSession(): VaultSession {
        return sessionManager.getActive()
            ?: throw IllegalStateException("Vault is locked")
    }

    suspend fun createNote(
        id: String = UUID.randomUUID().toString(),
        text: String,
        checklist: List<ChecklistItem> = emptyList(),
        labels: List<String> = emptyList(),
        attachments: List<NoteAttachment> = emptyList(),
        pinned: Boolean = false,
        expiresAt: Long? = null,
        readOnce: Boolean = false
    ): String {
        val session = requireSession()
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        return repo.saveNote(id, text, checklist, labels, attachments, pinned, session.noteKey, expiresAt, readOnce)
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

    suspend fun updateNote(note: Note): Boolean {
        val session = requireSession()
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        return repo.updateNote(note, session.noteKey)
    }

    suspend fun deleteNote(id: String) {
        val session = requireSession()
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        val note = repo.getNoteById(id, session.noteKey)
        if (note != null) {
            deleteNoteInternal(note, session)
        }
    }

    suspend fun sweepExpired(vacuum: Boolean = false) {
        val session = requireSession()
        val sweeper = SelfDestructService(session.database)
        sweeper.sweepExpired(vacuum = vacuum)
    }

    suspend fun storeAttachments(noteId: String, uris: List<Uri>): List<NoteAttachment> {
        if (uris.isEmpty()) return emptyList()
        val session = requireSession()
        val profileId = sessionManager.getActiveProfile()?.id ?: VaultProfile.REAL.id
        val resolver = sessionManager.context.contentResolver
        return uris.mapNotNull { uri ->
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return@mapNotNull null
            val id = UUID.randomUUID().toString()
            val ciphertext = noteCrypto.encrypt(bytes, session.noteKey)
            attachmentStore.writeEncrypted(profileId, noteId, id, ciphertext)
            bytes.fill(0)
            NoteAttachment(
                id = id,
                name = resolveName(resolver, uri) ?: "image",
                mimeType = resolver.getType(uri) ?: "application/octet-stream",
                byteCount = bytes.size.toLong()
            )
        }
    }

    suspend fun loadAttachment(noteId: String, attachmentId: String): ByteArray? {
        val session = requireSession()
        val profileId = sessionManager.getActiveProfile()?.id ?: VaultProfile.REAL.id
        val ciphertext = attachmentStore.readEncrypted(profileId, noteId, attachmentId) ?: return null
        return noteCrypto.decrypt(ciphertext, session.noteKey)
    }

    suspend fun removeAttachment(noteId: String, attachmentId: String): Boolean {
        val session = requireSession()
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        val note = repo.getNoteById(noteId, session.noteKey) ?: return false
        val updatedAttachments = note.attachments.filterNot { it.id == attachmentId }
        if (updatedAttachments.size == note.attachments.size) return false
        val updatedNote = note.copy(attachments = updatedAttachments)
        val ok = repo.updateNote(updatedNote, session.noteKey)
        if (ok) {
            val profileId = sessionManager.getActiveProfile()?.id ?: VaultProfile.REAL.id
            attachmentStore.deleteAttachment(profileId, noteId, attachmentId)
        }
        return ok
    }

    private suspend fun deleteNoteInternal(note: Note, session: VaultSession) {
        val profileId = sessionManager.getActiveProfile()?.id ?: VaultProfile.REAL.id
        attachmentStore.deleteAll(profileId, note.id)
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)
        repo.deleteNote(note.id)
    }

    private fun resolveName(resolver: android.content.ContentResolver, uri: Uri): String? {
        val cursor = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) return it.getString(index)
            }
        }
        return null
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
