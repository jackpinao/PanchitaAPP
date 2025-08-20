package com.pinao.panchitaapp.presentation.ui.guiaremision

import com.pinao.panchitaapp.domain.model.ProductModel

sealed interface ProductsUiState {
    object Loading : ProductsUiState
    data class Error(val throwable: Throwable): ProductsUiState
    data class Success(val productsModelList: List<ProductModel>) : ProductsUiState
}