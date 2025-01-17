package com.pinao.panchitaapp.presentation.ui.home

//import com.pinao.panchitaapp.presentation.common.UtilsAdmob
import androidx.lifecycle.ViewModel


class HomeViewModel : ViewModel() {

//    @Inject
//    lateinit var utilsAdmob: UtilsAdmob

    fun getGreeting(): String {
        return "Hello Android!"
    }

//    fun initAdmob() {
//        //utilsAdmob.interstitial?.show(requireActivity())
//        utilsAdmob.initInterstitialAd()
//    }
}