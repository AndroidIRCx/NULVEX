package com.androidircx.nulvex.pro

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.data.VaultService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptedBackupServiceTest {

    @Test
    fun uploadEncryptedBackup_storesRegistryRecordAndReturnsDigest() = runTest {
        val vaultService = mockk<VaultService>()
        val sharedKeyStore = mockk<SharedKeyStore>()
        val backupRegistryStore = mockk<BackupRegistryStore>(relaxed = true)
        val apiClient = mockk<LaravelMediaApiClient>()

        coEvery { vaultService.exportBackupJsonBytes() } returns "{\"notes\":[1]}".toByteArray()
        every { sharedKeyStore.getKeyMaterial("key-1") } answers { ByteArray(32) { 7 } }
        every { apiClient.requestUpload("file", "application/octet-stream") } returns UploadRequestToken(
            id = "media-1",
            uploadToken = "upload-token",
            expires = 111L,
            downloadToken = "download-1",
            downloadExpires = 222L
        )
        every { apiClient.upload("media-1", "upload-token", 111L, any()) } returns true
        every {
            backupRegistryStore.add(
                mediaId = "media-1",
                downloadPathId = "download-1",
                keyId = "key-1",
                sizeBytes = any(),
                sha256 = any(),
                downloadToken = "download-1",
                downloadExpires = 222L
            )
        } returns BackupRecord(
            id = "r1",
            mediaId = "media-1",
            downloadPathId = "download-1",
            keyId = "key-1",
            downloadToken = "download-1",
            downloadExpires = 222L,
            sizeBytes = 0,
            sha256 = "",
            createdAt = 0L
        )

        val service = EncryptedBackupService(vaultService, sharedKeyStore, backupRegistryStore, apiClient)
        val result = service.uploadEncryptedBackup("key-1")

        assertEquals("media-1", result.mediaId)
        assertTrue(result.sizeBytes > 0)
        assertEquals(64, result.sha256.length)
        verify { apiClient.upload("media-1", "upload-token", 111L, any()) }
        verify {
            backupRegistryStore.add(
                mediaId = "media-1",
                downloadPathId = "download-1",
                keyId = "key-1",
                sizeBytes = any(),
                sha256 = any(),
                downloadToken = "download-1",
                downloadExpires = 222L
            )
        }
    }

    @Test
    fun restoreFromStoredRecord_usesDownloadPathIdAndRecordKey() = runTest {
        val vaultService = mockk<VaultService>()
        val sharedKeyStore = mockk<SharedKeyStore>()
        val backupRegistryStore = mockk<BackupRegistryStore>()
        val apiClient = mockk<LaravelMediaApiClient>()

        val service = EncryptedBackupService(vaultService, sharedKeyStore, backupRegistryStore, apiClient)

        coEvery { vaultService.exportBackupJsonBytes() } returns "{\"notes\":[]}".toByteArray()
        every { sharedKeyStore.getKeyMaterial("key-restore") } answers { ByteArray(32) { 9 } }
        val wrapper = service.buildEncryptedBackupWrapper("key-restore")

        every { backupRegistryStore.getById("rec-1") } returns BackupRecord(
            id = "rec-1",
            mediaId = "media-a",
            downloadPathId = "download-path-a",
            keyId = "key-restore",
            downloadToken = "d-token",
            downloadExpires = 999L,
            sizeBytes = 12,
            sha256 = "hash",
            createdAt = 1L
        )

        every { apiClient.download("download-path-a", "d-token", 999L) } returns wrapper
        every { sharedKeyStore.getKeyMaterial("key-restore") } answers { ByteArray(32) { 9 } }
        coEvery { vaultService.importBackupJsonBytes(any(), merge = false) } returns 4

        val imported = service.restoreFromStoredRecord("rec-1", merge = false)

        assertEquals(4, imported)
        verify { apiClient.download("download-path-a", "d-token", 999L) }
    }
}
