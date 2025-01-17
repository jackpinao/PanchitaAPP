package com.pinao.panchitaapp.navigation

import androidx.navigation.NavController

object AllDestinations {
    const val HOME_ROUTE = "home"
    const val LOGIN_ROUTE = "login"
    const val RECARGA = "recarga"
}

class AppNavigationActions(
    private val navController: NavController
) {
    fun navigateToHome() {
        navController.navigate(AllDestinations.HOME_ROUTE) {
            popUpTo(AllDestinations.HOME_ROUTE) {
                inclusive = true
            }
        }
    }

    fun navigateToLogin() {
        navController.navigate(AllDestinations.LOGIN_ROUTE)
    }

    fun navigateToRecarga() {
        navController.navigate(AllDestinations.RECARGA){
            launchSingleTop = true
            restoreState = true
        }
    }
}