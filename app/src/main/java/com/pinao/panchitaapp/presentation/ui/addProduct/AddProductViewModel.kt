package com.pinao.panchitaapp.presentation.ui.addProduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.usecase.category.CategoryUseCases
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.domain.usecase.products.ScanBarcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddProductUiState(
    val productCode: String = "",
    val productName: String = "",
    val productPrice: String = "",
    val productCategory: String = "",
    val productStock: String = "",
    val listOfCategories: List<String> = emptyList(),
    val expanded: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AddProductViewModel(
    private val productUseCases: ProductUseCases,
    private val categoryUseCases: CategoryUseCases,
    private val scanBarcodeUseCase: ScanBarcodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(productName = newName) }
    }

    fun onPriceChange(newPrice: String) {
        if (newPrice.isEmpty() || newPrice.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(productPrice = newPrice) }
        }
    }

    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(productCategory = newCategory) }
    }

    fun onStockChange(newStock: String) {
        if (newStock.isEmpty() || newStock.matches(Regex("^\\d+$"))) {
            _uiState.update { it.copy(productStock = newStock) }
        }
    }

    fun onCodeChanged(newCode: String) {
        _uiState.update { it.copy(productCode = newCode) }
    }

    init {
        listCategories()
    }

    fun startScanning() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val scannedCode = scanBarcodeUseCase()
            
            if (scannedCode != null) {
                _uiState.update { 
                    it.copy(
                        productCode = scannedCode, 
                        isLoading = false 
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false 
                        // Opcional: Mostrar error de "Cancelado" si lo deseas
                    ) 
                }
            }
        }
    }

    fun listCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                categoryUseCases.getAll()
                    .collect { categories ->
                        val categoryNames = categories.map { it.name }
                        _uiState.update {
                            it.copy(
                                listOfCategories = categoryNames,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al obtener las categorías: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onErrorShow() {
        _uiState.update { it.copy(error = null) }
    }
}
