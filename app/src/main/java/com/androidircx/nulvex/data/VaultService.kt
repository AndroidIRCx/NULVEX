package com.androidircx.nulvex.data

import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import com.androidircx.nulvex.crypto.NoteCrypto
import com.androidircx.nulvex.crypto.XChaCha20Poly1305NoteCrypto
import com.androidircx.nulvex.security.VaultKeyManager
import com.androidircx.nulvex.security.VaultProfile
import com.androidircx.nulvex.security.wipe
import org.json.JSONArray
import org.json.JSONObject
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

    suspend fun exportBackupJsonBytes(): ByteArray {
        val notes = listNotes()
        val root = JSONObject().apply {
            put("v", 1)
            put("exportedAt", System.currentTimeMillis())
            put("profileId", sessionManager.getActiveProfile()?.id ?: VaultProfile.REAL.id)
        }
        val notesArray = JSONArray()
        for (note in notes) {
            val noteObj = JSONObject().apply {
                put("id", note.id)
                put("text", note.text)
                put("pinned", note.pinned)
                put("createdAt", note.createdAt)
                put("expiresAt", note.expiresAt ?: JSONObject.NULL)
                put("readOnce", note.readOnce)
            }

            val checklistArray = JSONArray()
            note.checklist.forEach { item ->
                checklistArray.put(
                    JSONObject().apply {
                        put("id", item.id)
                        put("text", item.text)
                        put("checked", item.checked)
                    }
                )
            }
            noteObj.put("checklist", checklistArray)

            val labelsArray = JSONArray()
            note.labels.forEach { label -> labelsArray.put(label) }
            noteObj.put("labels", labelsArray)

            val attachmentArray = JSONArray()
            note.attachments.forEach { attachment ->
                val bytes = loadAttachment(note.id, attachment.id)
                val b64 = if (bytes != null) {
                    Base64.encodeToString(bytes, Base64.NO_WRAP).also { bytes.fill(0) }
                } else {
                    ""
                }
                attachmentArray.put(
                    JSONObject().apply {
                        put("id", attachment.id)
                        put("name", attachment.name)
                        put("mimeType", attachment.mimeType)
                        put("byteCount", attachment.byteCount)
                        put("data", b64)
                    }
                )
            }
            noteObj.put("attachments", attachmentArray)
            notesArray.put(noteObj)
        }
        root.put("notes", notesArray)
        return root.toString().toByteArray(Charsets.UTF_8)
    }

    suspend fun exportSingleNoteShareJsonBytes(noteId: String): ByteArray {
        val note = readNote(noteId) ?: throw IllegalArgumentException("Note not found")
        val root = JSONObject().apply {
            put("v", 1)
            put("type", "note-share")
            put("exportedAt", System.currentTimeMillis())
        }
        val noteObj = JSONObject().apply {
            put("id", note.id)
            put("text", note.text)
            put("pinned", note.pinned)
            put("createdAt", note.createdAt)
            put("expiresAt", note.expiresAt ?: JSONObject.NULL)
            put("readOnce", note.readOnce)
        }
        val checklistArray = JSONArray()
        note.checklist.forEach { item ->
            checklistArray.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("text", item.text)
                    put("checked", item.checked)
                }
            )
        }
        noteObj.put("checklist", checklistArray)
        val labelsArray = JSONArray()
        note.labels.forEach { label -> labelsArray.put(label) }
        noteObj.put("labels", labelsArray)
        val attachmentArray = JSONArray()
        note.attachments.forEach { attachment ->
            val bytes = loadAttachment(note.id, attachment.id)
            val b64 = if (bytes != null) {
                Base64.encodeToString(bytes, Base64.NO_WRAP).also { bytes.fill(0) }
            } else {
                ""
            }
            attachmentArray.put(
                JSONObject().apply {
                    put("id", attachment.id)
                    put("name", attachment.name)
                    put("mimeType", attachment.mimeType)
                    put("byteCount", attachment.byteCount)
                    put("data", b64)
                }
            )
        }
        noteObj.put("attachments", attachmentArray)
        root.put("note", noteObj)
        return root.toString().toByteArray(Charsets.UTF_8)
    }

    suspend fun importBackupJsonBytes(raw: ByteArray, merge: Boolean): Int {
        val session = requireSession()
        val root = JSONObject(raw.toString(Charsets.UTF_8))
        // Support both full backup ({"notes":[...]}) and single note-share ({"note":{...}})
        val notesArray = when {
            root.has("notes") -> root.optJSONArray("notes") ?: JSONArray()
            root.has("note") -> JSONArray().also { arr -> root.optJSONObject("note")?.let { arr.put(it) } }
            else -> JSONArray()
        }
        val repo = NoteRepository(session.database.noteDao(), noteCrypto)

        if (!merge) {
            val existing = repo.listNotes(session.noteKey)
            existing.forEach { note -> deleteNoteInternal(note, session) }
        }

        var imported = 0
        val profileId = sessionManager.getActiveProfile()?.id ?: VaultProfile.REAL.id
        for (i in 0 until notesArray.length()) {
            val noteObj = notesArray.optJSONObject(i) ?: continue
            val noteId = noteObj.optString("id", "").ifBlank { UUID.randomUUID().toString() }
            val text = noteObj.optString("text", "")
            val pinned = noteObj.optBoolean("pinned", false)
            val createdAt = noteObj.optLong("createdAt", System.currentTimeMillis())
            val expiresAt = noteObj.optLong("expiresAt").takeIf { noteObj.has("expiresAt") && !noteObj.isNull("expiresAt") }
            val readOnce = noteObj.optBoolean("readOnce", false)

            val checklist = mutableListOf<ChecklistItem>()
            val checklistArray = noteObj.optJSONArray("checklist") ?: JSONArray()
            for (j in 0 until checklistArray.length()) {
                val item = checklistArray.optJSONObject(j) ?: continue
                val id = item.optString("id", "")
                if (id.isBlank()) continue
                checklist.add(
                    ChecklistItem(
                        id = id,
                        text = item.optString("text", ""),
                        checked = item.optBoolean("checked", false)
                    )
                )
            }

            val labels = mutableListOf<String>()
            val labelsArray = noteObj.optJSONArray("labels") ?: JSONArray()
            for (j in 0 until labelsArray.length()) {
                val label = labelsArray.optString(j, "").trim()
                if (label.isNotBlank()) labels.add(label)
            }

            val attachments = mutableListOf<NoteAttachment>()
            val attachmentArray = noteObj.optJSONArray("attachments") ?: JSONArray()
            for (j in 0 until attachmentArray.length()) {
                val att = attachmentArray.optJSONObject(j) ?: continue
                val attId = att.optString("id", "")
                if (attId.isBlank()) continue

                val dataB64 = att.optString("data", "")
                if (dataB64.isNotBlank()) {
                    val plaintext = Base64.decode(dataB64, Base64.DEFAULT)
                    val ciphertext = noteCrypto.encrypt(plaintext, session.noteKey)
                    plaintext.fill(0)
                    attachmentStore.writeEncrypted(profileId, noteId, attId, ciphertext)
                }

                attachments.add(
                    NoteAttachment(
                        id = attId,
                        name = att.optString("name", "file"),
                        mimeType = att.optString("mimeType", "application/octet-stream"),
                        byteCount = att.optLong("byteCount", 0L)
                    )
                )
            }

            val payload = NotePayload(
                text = text,
                checklist = checklist,
                labels = labels,
                attachments = attachments,
                pinned = pinned
            )
            val plaintext = NotePayloadCodec.encode(payload).toByteArray(Charsets.UTF_8)
            val ciphertext = noteCrypto.encrypt(plaintext, session.noteKey)
            plaintext.fill(0)

            session.database.noteDao().upsert(
                NoteEntity(
                    id = noteId,
                    ciphertext = ciphertext,
                    createdAt = createdAt,
                    expiresAt = expiresAt,
                    readOnce = readOnce,
                    deleted = false
                )
            )
            imported++
        }
        return imported
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
