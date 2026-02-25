package com.androidircx.nulvex.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteEditHistoryTest {

    @Test
    fun `record change enables undo and clears redo`() {
        val history = NoteEditHistory()
        val noteId = "n1"

        history.recordDraftChange(
            noteId = noteId,
            previous = null,
            current = NoteEditState(text = "a", expiresAt = null)
        )
        history.recordDraftChange(
            noteId = noteId,
            previous = NoteEditState(text = "a", expiresAt = null),
            current = NoteEditState(text = "b", expiresAt = null)
        )

        assertTrue(history.canUndo(noteId))
        assertFalse(history.canRedo(noteId))
    }

    @Test
    fun `undo and redo return expected snapshots`() {
        val history = NoteEditHistory()
        val noteId = "n1"
        val a = NoteEditState(text = "a", expiresAt = null)
        val b = NoteEditState(text = "b", expiresAt = 1000L)
        val c = NoteEditState(text = "c", expiresAt = 2000L)

        history.recordDraftChange(noteId, previous = null, current = a)
        history.recordDraftChange(noteId, previous = a, current = b)
        history.recordDraftChange(noteId, previous = b, current = c)

        val undo1 = history.undo(noteId, current = c)
        val undo2 = history.undo(noteId, current = b)
        val undo3 = history.undo(noteId, current = a)
        assertEquals(b, undo1)
        assertEquals(a, undo2)
        assertNull(undo3)

        val redo1 = history.redo(noteId, current = a)
        val redo2 = history.redo(noteId, current = b)
        val redo3 = history.redo(noteId, current = c)
        assertEquals(b, redo1)
        assertEquals(c, redo2)
        assertNull(redo3)
    }

    @Test
    fun `new change after undo clears redo branch`() {
        val history = NoteEditHistory()
        val noteId = "n1"
        val a = NoteEditState(text = "a", expiresAt = null)
        val b = NoteEditState(text = "b", expiresAt = null)
        val c = NoteEditState(text = "c", expiresAt = null)
        val x = NoteEditState(text = "x", expiresAt = null)

        history.recordDraftChange(noteId, previous = null, current = a)
        history.recordDraftChange(noteId, previous = a, current = b)
        history.recordDraftChange(noteId, previous = b, current = c)
        assertEquals(b, history.undo(noteId, current = c))

        history.recordDraftChange(noteId, previous = b, current = x)
        assertFalse(history.canRedo(noteId))
        assertNull(history.redo(noteId, current = x))
    }

    @Test
    fun `clear removes history for one note and all notes`() {
        val history = NoteEditHistory()
        val n1 = "n1"
        val n2 = "n2"
        val a = NoteEditState(text = "a", expiresAt = null)
        val b = NoteEditState(text = "b", expiresAt = null)

        history.recordDraftChange(n1, previous = null, current = a)
        history.recordDraftChange(n1, previous = a, current = b)
        history.recordDraftChange(n2, previous = null, current = a)
        history.recordDraftChange(n2, previous = a, current = b)

        history.clear(n1)
        assertFalse(history.canUndo(n1))
        assertTrue(history.canUndo(n2))

        history.clear()
        assertFalse(history.canUndo(n2))
    }

    @Test
    fun `undo stack is bounded per note`() {
        val history = NoteEditHistory(maxUndoEntriesPerNote = 2)
        val noteId = "n1"
        val a = NoteEditState(text = "a", expiresAt = null)
        val b = NoteEditState(text = "b", expiresAt = null)
        val c = NoteEditState(text = "c", expiresAt = null)
        val d = NoteEditState(text = "d", expiresAt = null)

        history.recordDraftChange(noteId, previous = null, current = a)
        history.recordDraftChange(noteId, previous = a, current = b)
        history.recordDraftChange(noteId, previous = b, current = c)
        history.recordDraftChange(noteId, previous = c, current = d)

        assertEquals(c, history.undo(noteId, current = d))
        assertEquals(b, history.undo(noteId, current = c))
        assertNull(history.undo(noteId, current = b))
    }
}
