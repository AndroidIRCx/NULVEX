package com.androidircx.nulvex.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SelfDestructService(
    private val database: NulvexDatabase,
    // Deletes a destroyed note's encrypted attachment blobs from the filesystem so
    // self-destructed / expired notes don't leave their attachments behind.
    private val attachmentDeleter: (suspend (String) -> Unit)? = null
) {
    suspend fun sweepExpired(now: Long = System.currentTimeMillis(), vacuum: Boolean = false) {
        val noteDao = database.noteDao()
        val expired = noteDao.listExpired(now)
        for (note in expired) {
            val zeroed = ByteArray(note.ciphertext.size)
            noteDao.overwriteCiphertext(note.id, zeroed)
            noteDao.softDelete(note.id)
            runCatching { attachmentDeleter?.invoke(note.id) }
        }
        noteDao.purgeDeleted()
        if (vacuum) {
            withContext(Dispatchers.IO) {
                database.openHelper.writableDatabase.execSQL("VACUUM")
            }
        }
    }
}
