package com.pinao.panchitaapp.navigation

sealed class AppScreens(val route: String){

    data object Home: AppScreens("home")
    data object Login: AppScreens("login")
    data object Recarga: AppScreens("recarga")
}