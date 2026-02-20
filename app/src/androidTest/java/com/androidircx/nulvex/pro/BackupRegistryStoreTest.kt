package com.androidircx.nulvex.pro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackupRegistryStoreTest {

    private lateinit var context: Context
    private lateinit var store: BackupRegistryStore

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("nulvex_backup_registry", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        store = BackupRegistryStore(context)
    }

    @Test
    fun addAndList_roundTrip() {
        val added = store.add(
            mediaId = "media-1",
            downloadPathId = "download-1",
            keyId = "key-1",
            sizeBytes = 128,
            sha256 = "abc123",
            downloadToken = "tok",
            downloadExpires = 123456L
        )

        val items = store.list()

        assertEquals(1, items.size)
        assertEquals(added.id, items.first().id)
        assertEquals("download-1", items.first().downloadPathId)
        assertEquals("tok", items.first().downloadToken)
        assertEquals(123456L, items.first().downloadExpires)
    }

    @Test
    fun getById_returnsRecordWhenExists() {
        val added = store.add(
            mediaId = "media-2",
            downloadPathId = "download-2",
            keyId = "key-2",
            sizeBytes = 256,
            sha256 = "def456",
            downloadToken = null,
            downloadExpires = null
        )

        val found = store.getById(added.id)

        assertNotNull(found)
        assertEquals("media-2", found!!.mediaId)
        assertEquals("download-2", found.downloadPathId)
    }

    @Test
    fun delete_removesRecord() {
        val added = store.add(
            mediaId = "media-3",
            downloadPathId = "download-3",
            keyId = "key-3",
            sizeBytes = 64,
            sha256 = "ghi789",
            downloadToken = null,
            downloadExpires = null
        )

        val removed = store.delete(added.id)

        assertTrue(removed)
        assertNull(store.getById(added.id))
    }
}
