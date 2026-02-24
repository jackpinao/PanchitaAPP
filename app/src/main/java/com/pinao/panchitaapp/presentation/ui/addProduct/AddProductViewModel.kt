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
import android.util.Log
import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.model.ProductModel
import java.util.UUID

data class AddProductUiState(
    val productCode: String = "",
    val productName: String = "",
    val productPurchasePrice: String = "",
    val productCategory: String = "",
    val productCategoryId: String = "",
    val productStock: String = "",
    val productRevenueCategory: Double = 0.0,
    val listOfCategoriesName: List<String> = emptyList(),
    val listOfCategoriesId: List<String> = emptyList(),
    val listOfCategoriesRevenue: List<Double> = emptyList(),
    val expanded: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateBack: Boolean = false
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
            _uiState.update { it.copy(productPurchasePrice = newPrice) }
        }
    }

    fun onCategoryChange(newCategoryName: String) {
        // Buscamos la posición del nombre para obtener el ID en la misma posición
        val index = _uiState.value.listOfCategoriesName.indexOf(newCategoryName)
        val categoryId = if (index != -1) _uiState.value.listOfCategoriesId[index] else ""
        val categoryRevenue = if (index != -1) _uiState.value.listOfCategoriesRevenue[index] else 0.0

        _uiState.update {
            it.copy(
                productCategory = newCategoryName,
                productCategoryId = categoryId,
                productRevenueCategory = categoryRevenue
            )
        }
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
            categoryUseCases.refreshCategories()
            try {
                categoryUseCases.getAll()
                    .collect { categories ->
                        val categoryNames = categories.map { it.name }
                        val categoryIds = categories.map { it.categoryId }
                        val categoryRevenue = categories.map { it.revenue }
                        _uiState.update {
                            it.copy(
                                listOfCategoriesName = categoryNames,
                                listOfCategoriesId = categoryIds,
                                listOfCategoriesRevenue = categoryRevenue,
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
                Log.e("AddProductViewModel", "Error al obtener las categorías", e)
            }
        }
    }

    fun saveProduct() {
        viewModelScope.launch {
            val state = _uiState.value

            // 1. Validaciones básicas
            if (state.productName.isBlank() || state.productPurchasePrice.isBlank() ||
                state.productCode.isBlank() || state.productCategoryId.isBlank()) {
                _uiState.update { it.copy(error = "Por favor, completa los campos obligatorios") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            val purchasePrice = state.productPurchasePrice.toDoubleOrNull() ?: 0.0
            val revenueCategory = state.productRevenueCategory
            val sellingPrice = purchasePrice + (purchasePrice * (revenueCategory / 100))

            val brandId = UUID.randomUUID().toString()

            try {

                val brand = BrandModel(
                    brandId = brandId,
                    name = "test"
                )

                // 2. Crear el objeto ProductModel
                val product = ProductModel(
                    name = state.productName,
                    brandId = brandId,// Acá se va guardar el Id de Marca- BRAND
                    barcode = state.productCode,
                    priceBuy = state.productPurchasePrice.toDoubleOrNull() ?: 0.0,
                    priceSell = sellingPrice,
                    priceExcludingIGV = sellingPrice - (sellingPrice * 0.18),
                    stockQuantity = state.productStock.toDoubleOrNull() ?: 0.0,
                    categoryId = state.productCategoryId // Aquí asumo que guardas el nombre o ID seleccionado,
                )


                // 3. Llamar al caso de uso
                productUseCases.save(product)

                // 4. Éxito: Detener carga y señalar navegación de regreso
                _uiState.update { it.copy(isLoading = false, navigateBack = true) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al guardar producto: ${e.message}",
                        isLoading = false
                    )
                }
                Log.e("AddProductViewModel", "Error al guardar", e)
            }
        }
    }

    fun onErrorShow() {
        _uiState.update { it.copy(error = null) }
    }
}
