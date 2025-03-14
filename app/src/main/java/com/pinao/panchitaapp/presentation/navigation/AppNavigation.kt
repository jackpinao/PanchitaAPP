package com.pinao.panchitaapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.presentation.ui.home.HomeScreen
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginScreen
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel

@Composable
fun AppNavigation(
    homeViewModel: HomeViewModel = HomeViewModel(),
    loginViewModel: LoginViewModel = LoginViewModel()
) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.Home.route) {
        composable(AppScreens.Home.route) {
            HomeScreen(homeViewModel = homeViewModel, navController = navController)
        }
        composable(AppScreens.Login.route) {
            LoginScreen(loginViewModel = loginViewModel, navController = navController)
        }
    }

}

