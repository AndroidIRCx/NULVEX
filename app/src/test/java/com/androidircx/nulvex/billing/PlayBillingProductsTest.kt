package com.androidircx.nulvex.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayBillingProductsTest {

    @Test
    fun productIds_usePlayConsoleDashFormat() {
        assertEquals("remove-ads-lifetime", PlayBillingProducts.REMOVE_ADS_ONE_TIME)
        assertEquals("pro-features-lifetime", PlayBillingProducts.PRO_FEATURES_ONE_TIME)
    }

    @Test
    fun oneTimeProducts_containsExpectedIds() {
        assertEquals(
            listOf(
                PlayBillingProducts.REMOVE_ADS_ONE_TIME,
                PlayBillingProducts.PRO_FEATURES_ONE_TIME
            ),
            PlayBillingProducts.oneTimeProducts
        )
    }

    @Test
    fun asQueryProducts_mapsAllAsInApp() {
        val products = PlayBillingProducts.asQueryProducts()

        assertEquals(PlayBillingProducts.oneTimeProducts.size, products.size)
        assertTrue(products.isNotEmpty())
    }
}
