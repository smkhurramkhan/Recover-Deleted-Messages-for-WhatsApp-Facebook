package com.test.recovermessages.utils

import android.content.Context
import android.util.Log
import android.widget.RelativeLayout
import com.google.android.gms.ads.*
import com.test.recovermessages.R

object AdmobHelper {
    var ADSHOWN = true
    fun showSmartBannerAd(context: Context, container: RelativeLayout, isShown: Boolean) {
        if (isShown) {
            val mAdView = AdView(context)
            mAdView.adSize = AdSize.SMART_BANNER
            mAdView.adUnitId = context.getString(R.string.banner_id)
            val adRequest = AdRequest.Builder().addTestDevice("D16EFB31DFAECAA980C195EFA7ECD53F")
                .build()
            mAdView.loadAd(adRequest)
            container.addView(mAdView)
            mAdView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    // Code to be executed when an ad request fails.
                }

                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                override fun onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                override fun onAdClosed() {
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                }
            }
        }
    }

    fun showInterstitialAd(context: Context, isShown: Boolean) {
        if (isShown) {
            val mInterstitialAd = InterstitialAd(context)
            mInterstitialAd.adUnitId = context.getString(R.string.interstitial_id)
            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(i: Int) {
                    super.onAdFailedToLoad(i)
                    Log.e("Interstitial Ad:-", "Ad Failed To Load")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    if (mInterstitialAd.isLoaded) mInterstitialAd.show()
                }
            }
            val adRequest = AdRequest.Builder().addTestDevice("D16EFB31DFAECAA980C195EFA7ECD53F")
                .build()
            mInterstitialAd.loadAd(adRequest)
        }
    }
}