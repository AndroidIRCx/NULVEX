package com.androidircx.nulvex.ui

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelLabelsAndExpiryEditTest {

    private lateinit var app: Application
    private lateinit var vm: MainViewModel

    @Before
    fun setUp() = runBlocking {
        app = ApplicationProvider.getApplicationContext()
        VaultServiceLocator.init(app)
        VaultServiceLocator.panicWipeService().wipeAll()
        app.getSharedPreferences("nulvex_app_settings", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        vm = MainViewModel(app)
    }

    @Test
    fun createStandaloneLabel_addsLabelWithoutAnyNote() {
        vm.createStandaloneLabel("Work")

        assertTrue(vm.uiState.value.savedLabels.contains("Work"))
    }
}
