package com.androidircx.nulvex.ui

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import org.junit.Assert.assertEquals
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

        app.getSharedPreferences("nulvex_shared_keys", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

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
}
