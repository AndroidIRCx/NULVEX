package com.androidircx.nulvex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SyncStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOutbox(op: SyncOutboxEntity)

    @Query(
        """
        SELECT * FROM sync_outbox
        WHERE nextAttemptAt <= :now
        ORDER BY createdAt ASC
        LIMIT :limit
        """
    )
    suspend fun listReadyOutbox(now: Long, limit: Int): List<SyncOutboxEntity>

    @Query("DELETE FROM sync_outbox WHERE opId = :opId")
    suspend fun deleteOutbox(opId: String): Int

    @Query(
        """
        UPDATE sync_outbox
        SET attemptCount = :attemptCount, nextAttemptAt = :nextAttemptAt
        WHERE opId = :opId
        """
    )
    suspend fun updateOutboxRetry(opId: String, attemptCount: Int, nextAttemptAt: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCursor(cursor: SyncCursorEntity)

    @Query("SELECT * FROM sync_cursor WHERE profile = :profile LIMIT 1")
    suspend fun getCursor(profile: String): SyncCursorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConflict(conflict: SyncConflictEntity)

    @Query(
        """
        SELECT * FROM sync_conflicts
        WHERE profile = :profile AND resolvedAt IS NULL
        ORDER BY createdAt DESC
        """
    )
    suspend fun listOpenConflicts(profile: String): List<SyncConflictEntity>

    @Query("UPDATE sync_conflicts SET resolvedAt = :resolvedAt WHERE id = :id")
    suspend fun markConflictResolved(id: String, resolvedAt: Long): Int
}
