package com.androidircx.nulvex.reminder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NoteReminderSchedulerContractTest {

    private fun request(
        noteId: String,
        triggerAt: Long
    ) = ReminderRequest(
        noteId = noteId,
        triggerAtEpochMillis = triggerAt,
        title = "Title $noteId",
        preview = "Preview $noteId"
    )

    @Test
    fun scheduleStoresRequest() {
        val scheduler = InMemoryNoteReminderScheduler()

        scheduler.schedule(request(noteId = "n1", triggerAt = 1000L))

        val stored = scheduler.get("n1")
        assertEquals(1000L, stored?.triggerAtEpochMillis)
        assertEquals(1, scheduler.size())
    }

    @Test
    fun scheduleOverwritesExistingRequestForSameNote() {
        val scheduler = InMemoryNoteReminderScheduler()

        scheduler.schedule(request(noteId = "n1", triggerAt = 1000L))
        scheduler.schedule(request(noteId = "n1", triggerAt = 2000L))

        val stored = scheduler.get("n1")
        assertEquals(2000L, stored?.triggerAtEpochMillis)
        assertEquals(1, scheduler.size())
    }

    @Test
    fun cancelRemovesOnlyTargetNote() {
        val scheduler = InMemoryNoteReminderScheduler()
        scheduler.schedule(request(noteId = "n1", triggerAt = 1000L))
        scheduler.schedule(request(noteId = "n2", triggerAt = 2000L))

        scheduler.cancel("n1")

        assertNull(scheduler.get("n1"))
        assertEquals(1, scheduler.size())
        assertEquals(2000L, scheduler.get("n2")?.triggerAtEpochMillis)
    }

    @Test
    fun cancelAllClearsEveryReminder() {
        val scheduler = InMemoryNoteReminderScheduler()
        scheduler.schedule(request(noteId = "n1", triggerAt = 1000L))
        scheduler.schedule(request(noteId = "n2", triggerAt = 2000L))

        scheduler.cancelAll()

        assertEquals(0, scheduler.size())
    }
}
