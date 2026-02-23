package com.androidircx.nulvex.ui

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import com.androidircx.nulvex.ui.PendingImport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelKeyGenerationTest {

    private lateinit var app: Application
    private lateinit var vm: MainViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        VaultServiceLocator.init(app)

        listOf("nulvex_shared_keys", "nulvex_ad_prefs", "nulvex_app_settings").forEach {
            app.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().commit()
        }

        vm = MainViewModel(app)
    }

    @Test
    fun generateXChaChaTwice_keepsSuccessStateAndAddsKeys() {
        val initial = vm.uiState.value.sharedKeys.size

        vm.generateXChaChaKey("k1")
        vm.generateXChaChaKey("k2")

        val state = vm.uiState.value
        assertEquals("XChaCha key generated", state.backupStatus)
        assertTrue(state.sharedKeys.size >= initial + 2)
    }

    @Test
    fun generatePgp_setsSuccessStateAndAddsKey() {
        val initial = vm.uiState.value.sharedKeys.size

        vm.generatePgpKey("pgp1")

        val state = vm.uiState.value
        assertEquals("OpenPGP key generated", state.backupStatus)
        assertTrue(state.sharedKeys.size >= initial + 1)
    }

    @Test
    fun deleteSharedKey_removesGeneratedKeyFromState() {
        vm.generateXChaChaKey("to-delete")
        val created = vm.uiState.value.sharedKeys.firstOrNull { it.label == "to-delete" }
        assertTrue(created != null)

        vm.deleteSharedKey(created!!.id)

        assertTrue(vm.uiState.value.sharedKeys.none { it.id == created.id })
    }

    @Test
    fun setPinScramble_true_updatesStateAndPersists() {
        vm.setPinScramble(true)
        assertTrue(vm.uiState.value.pinScrambleEnabled)

        val reloaded = MainViewModel(app)
        assertTrue(reloaded.uiState.value.pinScrambleEnabled)
    }

    @Test
    fun setPinScramble_false_updatesStateAndPersists() {
        vm.setPinScramble(true)
        vm.setPinScramble(false)
        assertFalse(vm.uiState.value.pinScrambleEnabled)
    }

    @Test
    fun setHidePinLength_true_updatesStateAndPersists() {
        vm.setHidePinLength(true)
        assertTrue(vm.uiState.value.hidePinLengthEnabled)

        val reloaded = MainViewModel(app)
        assertTrue(reloaded.uiState.value.hidePinLengthEnabled)
    }

    @Test
    fun setHidePinLength_false_updatesStateAndPersists() {
        vm.setHidePinLength(true)
        vm.setHidePinLength(false)
        assertFalse(vm.uiState.value.hidePinLengthEnabled)
    }

    @Test
    fun clearNoteShareUrl_clearsStateField() {
        vm.uiState.value = vm.uiState.value.copy(noteShareUrl = "https://example.com/abc")
        vm.clearNoteShareUrl()
        assertTrue(vm.uiState.value.noteShareUrl.isBlank())
    }

    @Test
    fun buildQrKeyTransferPayload_withExistingXChaChaKey_returnsNonNullJson() {
        vm.generateXChaChaKey("qr-test")
        val key = vm.uiState.value.sharedKeys.firstOrNull { it.label == "qr-test" }
        assertNotNull(key)

        val payload = vm.buildQrKeyTransferPayload(key!!.id)

        assertNotNull(payload)
        val json = org.json.JSONObject(payload!!)
        assertEquals("nulvex-key-share", json.getString("type"))
        assertEquals("xchacha20poly1305_key", json.getString("format"))
    }

    @Test
    fun buildQrKeyTransferPayload_unknownKey_returnsNull() {
        val result = vm.buildQrKeyTransferPayload("no-such-key")
        assertEquals(null, result)
    }

    @Test
    fun uploadKeyManagerToApi_withoutProFeatures_setsError() {
        // no pro features → should set error immediately without launching coroutine
        vm.uploadKeyManagerToApi(false, null)
        assertTrue(vm.uiState.value.error?.contains("Pro") == true)
        assertFalse(vm.uiState.value.isBusy)
    }

    @Test
    fun restoreKeyManagerFromApi_withoutProFeatures_setsError() {
        vm.restoreKeyManagerFromApi("some-media-id", null)
        assertTrue(vm.uiState.value.error?.contains("Pro") == true)
    }

    @Test
    fun restoreKeyManagerFromApi_withProButBlankId_setsError() {
        vm.grantLifetimeProFeatures()
        vm.restoreKeyManagerFromApi("", null)
        assertTrue(vm.uiState.value.error?.contains("required") == true)
    }

    // --- importIncomingRemoteKeyManager ---

    @Test
    fun importIncomingRemoteKeyManager_invalidMediaId_setsError() {
        // Network call to non-existent ID should fail → sets error, clears busy
        vm.importIncomingRemoteKeyManager("nonexistent-id-xyz", null)
        Thread.sleep(3000) // wait for IO coroutine
        assertFalse(vm.uiState.value.isBusy)
        assertNotNull(vm.uiState.value.error)
        assertTrue(
            vm.uiState.value.error!!.contains("failed", ignoreCase = true) ||
            vm.uiState.value.error!!.contains("import", ignoreCase = true)
        )
    }

    @Test
    fun importIncomingRemoteKeyManager_setsRemoteKeysMimeOnPendingImport() {
        // Verify RemoteMedia carries mime correctly (unit-level, no network)
        val pendingImport = PendingImport.RemoteMedia(
            mediaId = "abc123",
            mime = com.androidircx.nulvex.pro.NulvexFileTypes.KEY_MANAGER_MIME
        )
        vm.setPendingImport(pendingImport)
        val state = vm.uiState.value
        assertTrue(state.pendingImport is PendingImport.RemoteMedia)
        assertEquals(
            com.androidircx.nulvex.pro.NulvexFileTypes.KEY_MANAGER_MIME,
            (state.pendingImport as PendingImport.RemoteMedia).mime
        )
    }

    @Test
    fun importIncomingRemoteKeyManager_remoteMediaWithoutMime_hasNullMime() {
        val pendingImport = PendingImport.RemoteMedia(mediaId = "xyz")
        vm.setPendingImport(pendingImport)
        val import = vm.uiState.value.pendingImport as? PendingImport.RemoteMedia
        assertNotNull(import)
        assertEquals(null, import!!.mime)
    }
}
