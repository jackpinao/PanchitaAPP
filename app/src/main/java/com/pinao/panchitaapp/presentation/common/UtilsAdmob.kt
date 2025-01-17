package com.pinao.panchitaapp.presentation.common

//import android.content.Context
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.LoadAdError
//import com.google.android.gms.ads.interstitial.InterstitialAd
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
//import com.pinao.panchitaapp.R
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Inject
//import javax.inject.Singleton

//@Singleton
//class UtilsAdmob @Inject constructor(@ApplicationContext private val context: Context) {
//
//    var interstitial: InterstitialAd? = null
//
//    fun initInterstitialAd() {
//        InterstitialAd.load(
//            context,
//            context.getString(R.string.id_admob_app),
//            AdRequest.Builder().build(),
//            object : InterstitialAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    interstitial = null
//                }
//
//                override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                    interstitial = interstitialAd
//                }
//            }
//        )
//    }
//}