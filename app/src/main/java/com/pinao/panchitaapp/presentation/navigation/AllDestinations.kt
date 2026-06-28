package com.pinao.panchitaapp.presentation.navigation

import androidx.navigation.NavController

object AllDestinations {
    const val HOME_ROUTE = "home"
    const val LOGIN_ROUTE = "login"
    const val RECARGA = "recarga"
    const val GUIA_REMISION = "guiaRemision"
    const val PREVIEW_TICKET = "previewTicket"
    const val ADD_PRODUCT = "addProduct"
    const val ADD_CATEGORY = "addCategory"
    const val PRODUCT_SEARCH = "productSearch"
    const val INVENTORY_LIST = "inventoryList"
    const val FAST_SALE = "fastSale"
    const val FAST_SALE_PREVIEW = "fastSalePreview"
    const val SETTINGS = "settings"
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
    fun navigateToAddProduct(){
        navController.navigate(AllDestinations.ADD_PRODUCT) {
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToAddCategory() {
        navController.navigate(AllDestinations.ADD_CATEGORY) {
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToProductSearch(){
        navController.navigate(AllDestinations.PRODUCT_SEARCH) {
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToInventary(){
        navController.navigate(AllDestinations.INVENTORY_LIST){
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToFastSale() {
        navController.navigate(AllDestinations.FAST_SALE) {
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToFastSalePreview() {
        navController.navigate(AllDestinations.FAST_SALE_PREVIEW) {
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToSettings() {
        navController.navigate(AllDestinations.SETTINGS) {
            launchSingleTop = true
            restoreState = true
        }
    }
}