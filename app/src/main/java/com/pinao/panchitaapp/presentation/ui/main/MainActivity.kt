package com.pinao.panchitaapp.presentation.ui.main

//import com.pinao.panchitaapp.presentation.common.UtilsAdmob
//import androidx.activity.viewModels
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pinao.panchitaapp.presentation.navigation.AppNavGraph
import com.pinao.panchitaapp.presentation.theme.resource.PanchitaAPPTheme

//import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

//    @Inject
//    lateinit var utilsAdmob: UtilsAdmob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PanchitaAPPTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph()
//                    AppNavigation(
//                        homeViewModel = homeViewModel,
//                        loginViewModel = loginViewModel
//                    )
                }
            }
        }
//        utilsAdmob.initInterstitialAd()
    }
}


