package com.androidircx.nulvex.reminder

class InMemoryNoteReminderScheduler : NoteReminderScheduler {
    private val scheduledByNoteId = linkedMapOf<String, ReminderRequest>()

    override fun schedule(request: ReminderRequest) {
        scheduledByNoteId[request.noteId] = request
    }

    override fun cancel(noteId: String) {
        scheduledByNoteId.remove(noteId)
    }

    override fun cancelAll() {
        scheduledByNoteId.clear()
    }

    fun get(noteId: String): ReminderRequest? = scheduledByNoteId[noteId]

    fun size(): Int = scheduledByNoteId.size
}
