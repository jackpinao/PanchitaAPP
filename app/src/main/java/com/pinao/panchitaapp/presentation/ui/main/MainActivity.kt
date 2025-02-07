package com.pinao.panchitaapp.presentation.ui.main

//import com.pinao.panchitaapp.presentation.common.UtilsAdmob
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.navigation.AppNavGraph
import com.pinao.panchitaapp.presentation.theme.resource.PanchitaAPPTheme
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

//    @Inject
//    lateinit var utilsAdmob: UtilsAdmob

    private val homeViewModel: HomeViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val claroRecargaViewModel: ClaroRecargaViewModel by viewModels()
    //private val useCase: SaveRechangeUseCase by inject()

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


