package com.pinao.panchitaapp.presentation.ui.inventoryList

import androidx.lifecycle.ViewModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InventoryListViewModel(
    private val productUseCases: ProductUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryListUiState>(
        InventoryListUiState.Idle(emptyList())
    )
    val uiState = _uiState.asStateFlow()

    private fun updateState(reduce: (List<ProductModel>) -> List<ProductModel>) {
        _uiState.update { currentState ->
            val updatedList = reduce(currentState.inventoryList)

            (if (currentState is InventoryListUiState.Error) {
                InventoryListUiState.Idle(inventoryList = updatedList)
            } else {
                when (currentState) {
                    is InventoryListUiState.Idle -> currentState.copy(inventoryList = updatedList)
                    is InventoryListUiState.Loading -> currentState.copy(inventoryList = updatedList)
                    is InventoryListUiState.Success -> currentState.copy(inventoryList = updatedList)
                }
            })
        }
    }

    fun onNavigateToAddItem(){

    }

    fun onItemClick(productId: String) {}
    fun onDeleteClick(productId: String) {}
}