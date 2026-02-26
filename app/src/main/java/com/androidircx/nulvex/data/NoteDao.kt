package com.androidircx.nulvex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity): Long

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE deleted = 0 AND archivedAt IS NULL AND trashedAt IS NULL")
    suspend fun listActive(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE deleted = 0 AND archivedAt IS NOT NULL AND trashedAt IS NULL")
    suspend fun listArchived(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE expiresAt IS NOT NULL AND expiresAt <= :now AND deleted = 0 AND trashedAt IS NULL")
    suspend fun listExpired(now: Long): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE reminderAt IS NOT NULL AND reminderAt <= :now AND reminderDone = 0 AND deleted = 0 AND trashedAt IS NULL")
    suspend fun listDueReminders(now: Long): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE deleted = 0 AND trashedAt IS NOT NULL")
    suspend fun listTrashed(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE deleted = 0 AND trashedAt IS NOT NULL AND trashedAt <= :cutoff")
    suspend fun listTrashedBefore(cutoff: Long): List<NoteEntity>

    @Query("UPDATE notes SET ciphertext = :ciphertext WHERE id = :id")
    suspend fun overwriteCiphertext(id: String, ciphertext: ByteArray): Int

    @Query("UPDATE notes SET updatedAt = :updatedAt WHERE id = :id")
    suspend fun setUpdatedAt(id: String, updatedAt: Long): Int

    @Query("UPDATE notes SET deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String): Int

    @Query("UPDATE notes SET archivedAt = :archivedAt WHERE id = :id")
    suspend fun setArchivedAt(id: String, archivedAt: Long?): Int

    @Query("UPDATE notes SET reminderAt = :reminderAt, reminderDone = :reminderDone, reminderRepeat = :reminderRepeat WHERE id = :id")
    suspend fun setReminder(id: String, reminderAt: Long?, reminderDone: Boolean, reminderRepeat: String?): Int

    @Query("UPDATE notes SET trashedAt = :trashedAt, archivedAt = NULL, reminderAt = NULL, reminderDone = 0 WHERE id = :id")
    suspend fun setTrashedAt(id: String, trashedAt: Long?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(revision: NoteRevisionEntity): Long

    @Query("SELECT * FROM note_revisions WHERE noteId = :noteId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun listRevisions(noteId: String, limit: Int): List<NoteRevisionEntity>

    @Query("SELECT * FROM note_revisions WHERE noteId = :noteId AND id = :revisionId LIMIT 1")
    suspend fun getRevisionById(noteId: String, revisionId: String): NoteRevisionEntity?

    @Query(
        """
        DELETE FROM note_revisions
        WHERE noteId = :noteId
          AND id NOT IN (
            SELECT id FROM note_revisions
            WHERE noteId = :noteId
            ORDER BY createdAt DESC
            LIMIT :keep
          )
        """
    )
    suspend fun pruneRevisions(noteId: String, keep: Int): Int

    @Query(
        """
        UPDATE notes
        SET ciphertext = :ciphertext,
            expiresAt = :expiresAt,
            readOnce = :readOnce,
            archivedAt = :archivedAt,
            reminderAt = :reminderAt,
            reminderDone = :reminderDone
        WHERE id = :id
        """
    )
    suspend fun restoreFromRevision(
        id: String,
        ciphertext: ByteArray,
        expiresAt: Long?,
        readOnce: Boolean,
        archivedAt: Long?,
        reminderAt: Long?,
        reminderDone: Boolean
    ): Int

    @Query("DELETE FROM notes WHERE deleted = 1")
    suspend fun purgeDeleted(): Int
}
