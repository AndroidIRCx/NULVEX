package com.androidircx.nulvex.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NulvexFileTypesTest {

    @Test
    fun extensions_areStableAndUnique() {
        val exts = setOf(
            NulvexFileTypes.NOTE_SHARE_EXT,
            NulvexFileTypes.BACKUP_EXT,
            NulvexFileTypes.KEY_MANAGER_EXT
        )

        assertEquals(3, exts.size)
        assertTrue(NulvexFileTypes.NOTE_SHARE_EXT.startsWith("."))
        assertTrue(NulvexFileTypes.BACKUP_EXT.startsWith("."))
        assertTrue(NulvexFileTypes.KEY_MANAGER_EXT.startsWith("."))
    }

    @Test
    fun mimes_areStableAndUnique() {
        val mimes = setOf(
            NulvexFileTypes.NOTE_SHARE_MIME,
            NulvexFileTypes.BACKUP_MIME,
            NulvexFileTypes.KEY_MANAGER_MIME
        )

        assertEquals(3, mimes.size)
        assertTrue(NulvexFileTypes.NOTE_SHARE_MIME.startsWith("application/"))
        assertTrue(NulvexFileTypes.BACKUP_MIME.startsWith("application/"))
        assertTrue(NulvexFileTypes.KEY_MANAGER_MIME.startsWith("application/"))
    }
}
