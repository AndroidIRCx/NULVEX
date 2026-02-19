package com.androidircx.nulvex.ads

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Handles rewarded ad loading and showing.
 *
 * Ad unit constants:
 *  - [AD_UNIT_BANNER]           — top banner, always shown when not ad-free
 *  - [AD_UNIT_REWARDED_NO_ADS]  — watching grants 10 reward units (= 10 minutes ad-free)
 *  - [AD_UNIT_REWARDED_SHARE]   — watching grants 1 reward unit (= 1 extra share quota)
 */
class AdManager(private val adPreferences: AdPreferences) {

    // -------------------------------------------------------------------------
    // Banner
    // -------------------------------------------------------------------------

    /** True if the current session is within the ad-free window. */
    fun isAdFree(): Boolean = adPreferences.isAdFree()

    // -------------------------------------------------------------------------
    // Rewarded: no_ads
    // -------------------------------------------------------------------------

    /**
     * Loads and immediately shows the "no_ads" rewarded ad.
     * On success [onGranted] is called with the reward amount (10 units per view),
     * where each unit represents 1 minute of ad-free time.
     *
     * If the ad fails to load, [onGranted] is NOT called — the user is not
     * penalised for a network issue.
     */
    fun showRewardedNoAds(activity: Activity, onGranted: (rewardAmount: Int) -> Unit) {
        RewardedAd.load(
            activity,
            AD_UNIT_REWARDED_NO_ADS,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            // Ad failed to display — do nothing
                        }
                    }
                    ad.show(activity) { rewardItem ->
                        onGranted(rewardItem.amount)
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Silently fail — do not grant reward
                }
            }
        )
    }

    // -------------------------------------------------------------------------
    // Rewarded: share
    // -------------------------------------------------------------------------

    /**
     * Loads and immediately shows the "share" rewarded ad.
     * On success [onGranted] is called with the reward amount (1 unit per view),
     * which the caller should forward to the Laravel backend to increment the
     * user's share quota.
     *
     * TODO: wire the granted amount to the Laravel API once the backend is ready.
     */
    fun showRewardedShare(activity: Activity, onGranted: (rewardAmount: Int) -> Unit) {
        RewardedAd.load(
            activity,
            AD_UNIT_REWARDED_SHARE,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            // Ad failed to display — do nothing
                        }
                    }
                    ad.show(activity) { rewardItem ->
                        onGranted(rewardItem.amount)
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Silently fail
                }
            }
        )
    }

    companion object {
        /** Banner shown at the very top of the app. */
        const val AD_UNIT_BANNER = "ca-app-pub-5116758828202889/4370436210"

        /**
         * Rewarded: remove ads.
         * Configured in AdMob to give 10 reward units per view.
         * 1 unit = 1 minute → 10 units = 10 minutes ad-free.
         */
        const val AD_UNIT_REWARDED_NO_ADS = "ca-app-pub-5116758828202889/2851129547"

        /**
         * Rewarded: share quota.
         * Configured in AdMob to give 1 reward unit per view.
         * 1 unit = 1 additional share credit (consumed via Laravel API).
         */
        const val AD_UNIT_REWARDED_SHARE = "ca-app-pub-5116758828202889/8570794853"

        /** Duration granted per reward unit for the no_ads ad (1 minute). */
        const val AD_FREE_MILLIS_PER_UNIT = 60_000L
    }
}
