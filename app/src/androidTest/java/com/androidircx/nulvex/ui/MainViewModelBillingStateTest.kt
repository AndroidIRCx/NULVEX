package com.androidircx.nulvex.ui

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import com.androidircx.nulvex.billing.PlayBillingProducts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelBillingStateTest {

    private lateinit var app: Application
    private lateinit var vm: MainViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        VaultServiceLocator.init(app)
        app.getSharedPreferences("nulvex_ad_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        app.getSharedPreferences("nulvex_app_settings", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        vm = MainViewModel(app)
    }

    @Test
    fun openPurchases_setsPurchasesScreen() {
        vm.openPurchases()
        assertTrue(vm.uiState.value.screen is Screen.Purchases)
    }

    @Test
    fun closePurchases_setsSettingsScreen() {
        vm.closePurchases()
        assertTrue(vm.uiState.value.screen is Screen.Settings)
    }

    @Test
    fun setBillingReady_updatesState() {
        vm.setBillingReady(true)
        assertTrue(vm.uiState.value.billingReady)

        vm.setBillingReady(false)
        assertFalse(vm.uiState.value.billingReady)
    }

    @Test
    fun updateBillingPrice_updatesMatchingProductOnly() {
        vm.updateBillingPrice(PlayBillingProducts.REMOVE_ADS_ONE_TIME, "$1.99")
        assertEquals("$1.99", vm.uiState.value.removeAdsPrice)
        assertEquals("Unavailable", vm.uiState.value.proFeaturesPrice)

        vm.updateBillingPrice(PlayBillingProducts.PRO_FEATURES_ONE_TIME, "$4.99")
        assertEquals("$1.99", vm.uiState.value.removeAdsPrice)
        assertEquals("$4.99", vm.uiState.value.proFeaturesPrice)
    }

    @Test
    fun grantLifetimeRemoveAds_setsAdFreeState() {
        vm.grantLifetimeRemoveAds()
        assertTrue(vm.uiState.value.isAdFree)
    }

    @Test
    fun grantLifetimeProFeatures_enablesUnlimitedSharesBehavior() {
        vm.grantLifetimeProFeatures()
        assertTrue(vm.uiState.value.hasProFeatures)

        vm.grantShareCredits(5)
        assertEquals(0, vm.uiState.value.shareCredits)
    }

    @Test
    fun enableDecoyBiometric_updatesState() {
        vm.enableDecoyBiometric()
        assertTrue(vm.uiState.value.decoyBiometricEnabled)
    }

    @Test
    fun disableDecoyBiometric_updatesState() {
        vm.enableDecoyBiometric()
        vm.disableDecoyBiometric()
        assertFalse(vm.uiState.value.decoyBiometricEnabled)
    }
}
