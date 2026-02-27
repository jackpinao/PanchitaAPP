package com.pinao.panchitaapp.presentation.ui.inventoryList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InventoryListViewModel(
    private val productUseCases: ProductUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryListUiState>(
        InventoryListUiState.Loading(emptyList())
    )
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        observeProducts()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeProducts() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        productUseCases.getAll()
                    } else {
                        productUseCases.search(query)
                    }
                }
                .collectLatest { products ->
                    _uiState.update { InventoryListUiState.Success(products) }
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onItemClick(productId: String) {}
    
    fun onDeleteClick(productId: String) {
        viewModelScope.launch {
            val product = uiState.value.inventoryList.find { it.productId == productId }
            product?.let {
                productUseCases.delete(it)
            }
        }
    }

    fun onNavigateToAddItem(){
        
    }
}