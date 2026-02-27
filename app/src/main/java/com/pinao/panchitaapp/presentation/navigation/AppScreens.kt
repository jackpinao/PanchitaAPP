package com.pinao.panchitaapp.presentation.navigation

sealed class AppScreens(val route: String){

    data object Home: AppScreens("home")
    data object Login: AppScreens("login")
    data object Recarga: AppScreens("recarga")
    data object GuiaRemision: AppScreens("guiaRemision")
    data object PreviewTicket: AppScreens("previewTicket")
    data object AddProduct: AppScreens("addProduct")
    data object AddCategory: AppScreens("addCategory")
    data object ProductSearch: AppScreens("productSearch")
    data object InventoryList: AppScreens("inventoryList")

    companion object {
        const val ADD_PRODUCT_ROUTE = "addProduct"
    }
}