package com.pinao.panchitaapp.presentation.ui.inventoryList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class InventoryListViewModel(
    private val productUseCases: ProductUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryListUiState>(
        InventoryListUiState.Loading(emptyList())
    )
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _eventFlow = MutableSharedFlow<InventoryListEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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

    /**
     * Refreshes the products list by first trying to sync any unsaved products
     * and then fetching the latest data from the remote source.
     */
    fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { InventoryListUiState.Loading(it.inventoryList) }
            try {
                productUseCases.syncUnsyncedProducts()
                productUseCases.refreshProducts()
            } catch (e: Exception) {
                Log.e("InventoryListViewModel", "el error es: $e")
            } finally {
                _uiState.update { currentState ->
                    if (currentState is InventoryListUiState.Loading) {
                        InventoryListUiState.Success(currentState.inventoryList)
                    } else currentState
                }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onItemClick(productId: String) {
        viewModelScope.launch {
            val product = uiState.value.inventoryList.find { it.productId == productId }
            product?.let {
                _eventFlow.emit(InventoryListEvent.NavigateToEdit(it.barcode))
            }
        }
    }

    fun onDeleteClick(productId: String) {
        viewModelScope.launch {
            val product = uiState.value.inventoryList.find { it.productId == productId }
            product?.let {
                productUseCases.delete(it)
            }
        }
    }

    sealed class InventoryListEvent {
        data class NavigateToEdit(val barcode: String) : InventoryListEvent()
    }
}
