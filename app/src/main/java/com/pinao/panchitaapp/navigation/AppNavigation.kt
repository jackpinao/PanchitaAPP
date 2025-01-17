package com.pinao.panchitaapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pinao.panchitaapp.presentation.ui.home.HomeScreen
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginScreen
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel

@Composable
fun AppNavigation(
    homeViewModel: HomeViewModel,
    loginViewModel: LoginViewModel
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