package com.pinao.panchitaapp.presentation.ui.inventoryList

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.presentation.ui.login.UiText

sealed interface InventoryListUiState{
    val inventoryList: List<ProductModel>
    data class Idle(override val inventoryList: List<ProductModel>) : InventoryListUiState
    data class Success(override val inventoryList: List<ProductModel>) : InventoryListUiState
    data class Error(
        override val inventoryList: List<ProductModel>,
        val message: UiText
    ) : InventoryListUiState
    data class Loading(override val inventoryList: List<ProductModel>) : InventoryListUiState
}
