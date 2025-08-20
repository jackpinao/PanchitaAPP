package com.pinao.panchitaapp.presentation.navigation

import androidx.navigation.NavController

object AllDestinations {
    const val HOME_ROUTE = "home"
    const val LOGIN_ROUTE = "login"
    const val RECARGA = "recarga"
    const val GUIA_REMISION = "guiaRemision"
    const val PREVIEW_TICKET = "previewTicket"
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
    fun navigateToGuiaRemision() {
        navController.navigate(AllDestinations.GUIA_REMISION) {
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToPreviewTicket() {
        navController.navigate(AllDestinations.PREVIEW_TICKET) {
            launchSingleTop = true
            restoreState = true
        }
    }

}