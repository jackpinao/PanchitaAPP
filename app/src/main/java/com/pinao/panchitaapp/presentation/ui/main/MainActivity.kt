package com.pinao.panchitaapp.presentation.ui.main

//import com.pinao.panchitaapp.presentation.common.UtilsAdmob
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
//import androidx.activity.viewModels
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pinao.panchitaapp.presentation.navigation.AppNavGraph
import com.pinao.panchitaapp.presentation.theme.resource.PanchitaAPPTheme
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel

//import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

//    @Inject
//    lateinit var utilsAdmob: UtilsAdmob

    private val homeViewModel: HomeViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    private val claroRecargaViewModel: ClaroRecargaViewModel by viewModel()
    //private val useCase: SaveRechangeUseCase by inject()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PanchitaAPPTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        homeViewModel = homeViewModel,
                        loginViewModel = loginViewModel,
                        rechargeViewModel = claroRecargaViewModel
                    )
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


