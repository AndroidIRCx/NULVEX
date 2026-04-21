package com.androidircx.nulvex.security

import com.androidircx.nulvex.reminder.NoteReminderScheduler

fun executePanicWipeAll(
    panicWipeService: PanicWipeService,
    appPreferences: AppPreferences,
    reminderScheduler: NoteReminderScheduler,
    securityEventStore: SecurityEventStore
) {
    securityEventStore.record(SecurityEventStore.EVENT_PANIC_WIPE)
    appPreferences.getReminderSchedules().keys.forEach { noteId ->
        reminderScheduler.cancel(noteId)
    }
    appPreferences.clearReminderSchedules()
    appPreferences.clearPendingReminderAction()
    panicWipeService.wipeAll()
}
