package com.androidircx.nulvex.data

import android.content.Context
import java.io.File

class NoteAttachmentStore(
    private val context: Context
) {
    fun writeEncrypted(profileId: String, noteId: String, attachmentId: String, ciphertext: ByteArray) {
        val target = attachmentFile(profileId, noteId, attachmentId)
        target.parentFile?.mkdirs()
        target.writeBytes(ciphertext)
    }

    fun readEncrypted(profileId: String, noteId: String, attachmentId: String): ByteArray? {
        val target = attachmentFile(profileId, noteId, attachmentId)
        return if (target.exists()) target.readBytes() else null
    }

    fun deleteAttachment(profileId: String, noteId: String, attachmentId: String) {
        val target = attachmentFile(profileId, noteId, attachmentId)
        if (target.exists()) {
            target.delete()
        }
    }

    fun deleteAll(profileId: String, noteId: String) {
        val dir = noteDir(profileId, noteId)
        if (dir.exists()) {
            dir.listFiles()?.forEach { it.delete() }
            dir.delete()
        }
    }

    private fun attachmentFile(profileId: String, noteId: String, attachmentId: String): File {
        return File(noteDir(profileId, noteId), "$attachmentId.bin")
    }

    private fun noteDir(profileId: String, noteId: String): File {
        return File(File(File(context.filesDir, "attachments"), profileId), noteId)
    }
}
