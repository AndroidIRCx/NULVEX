package com.androidircx.nulvex.ui

import com.androidircx.nulvex.data.ChecklistItem

data class NoteEditState(
    val text: String,
    val expiresAt: Long?,
    val labels: List<String> = emptyList(),
    val checklist: List<ChecklistItem> = emptyList(),
    val reminderAt: Long? = null,
    val reminderDone: Boolean = false,
    val archivedAt: Long? = null
)

class NoteEditHistory(
    private val maxUndoEntriesPerNote: Int = 100
) {
    private val undo = mutableMapOf<String, ArrayDeque<NoteEditState>>()
    private val redo = mutableMapOf<String, ArrayDeque<NoteEditState>>()

    fun recordDraftChange(
        noteId: String,
        previous: NoteEditState?,
        current: NoteEditState
    ) {
        if (previous != null && previous != current) {
            val stack = undo.getOrPut(noteId) { ArrayDeque() }
            if (stack.lastOrNull() != previous) {
                if (stack.size >= maxUndoEntriesPerNote) {
                    stack.removeFirst()
                }
                stack.addLast(previous)
            }
            redo.getOrPut(noteId) { ArrayDeque() }.clear()
        } else {
            undo.getOrPut(noteId) { ArrayDeque() }
            redo.getOrPut(noteId) { ArrayDeque() }
        }
    }

    fun undo(noteId: String, current: NoteEditState): NoteEditState? {
        val undoStack = undo[noteId] ?: return null
        if (undoStack.isEmpty()) return null
        redo.getOrPut(noteId) { ArrayDeque() }.addLast(current)
        return undoStack.removeLast()
    }

    fun redo(noteId: String, current: NoteEditState): NoteEditState? {
        val redoStack = redo[noteId] ?: return null
        if (redoStack.isEmpty()) return null
        val undoStack = undo.getOrPut(noteId) { ArrayDeque() }
        if (undoStack.size >= maxUndoEntriesPerNote) {
            undoStack.removeFirst()
        }
        undoStack.addLast(current)
        return redoStack.removeLast()
    }

    fun canUndo(noteId: String): Boolean = undo[noteId]?.isNotEmpty() == true

    fun canRedo(noteId: String): Boolean = redo[noteId]?.isNotEmpty() == true

    fun clear(noteId: String? = null) {
        if (noteId == null) {
            undo.clear()
            redo.clear()
            return
        }
        undo.remove(noteId)
        redo.remove(noteId)
    }
}
