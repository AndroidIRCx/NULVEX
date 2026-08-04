package com.androidircx.nulvex.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class NoteAttachmentIdTest {

    @Test
    fun rejectsPathTraversalAndSeparators() {
        assertFalse(NoteAttachmentStore.isSafeId(""))
        assertFalse(NoteAttachmentStore.isSafeId(".."))
        assertFalse(NoteAttachmentStore.isSafeId("../../databases/nulvex"))
        assertFalse(NoteAttachmentStore.isSafeId("a/b"))
        assertFalse(NoteAttachmentStore.isSafeId("a\\b"))
        assertFalse(NoteAttachmentStore.isSafeId("has space"))
        assertFalse(NoteAttachmentStore.isSafeId("dot.name"))
    }

    @Test
    fun acceptsLegitimateIds() {
        assertTrue(NoteAttachmentStore.isSafeId(UUID.randomUUID().toString()))
        assertTrue(NoteAttachmentStore.isSafeId("real"))
        assertTrue(NoteAttachmentStore.isSafeId("decoy"))
        assertTrue(NoteAttachmentStore.isSafeId("note_123-abc"))
    }
}
