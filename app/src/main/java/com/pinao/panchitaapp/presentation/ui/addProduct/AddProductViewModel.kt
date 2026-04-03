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
import com.pinao.panchitaapp.domain.usecase.brand.BrandUseCases
import com.pinao.panchitaapp.domain.util.PriceUtils
import kotlinx.coroutines.flow.firstOrNull
import org.koin.android.annotation.KoinViewModel
import java.util.UUID

data class AddProductUiState(
    val productId: String? = null,
    val productCode: String = "",
    val productName: String = "",
    val productTotalCost: String = "",
    val calculatedUnitPrice: Double = 0.0,
    val existingStock: Double = 0.0,
    val existingPriceBuy: Double = 0.0,
    val finalCalculatedStock: Double = 0.0,
    val productCategory: String = "",
    val productCategoryId: String = "",
    val productBrand: String = "",
    val productBrandId: String = "",
    val productStock: String = "",
    val productRevenueCategory: Double = 0.0,
    val listOfCategoriesName: List<String> = emptyList(),
    val listOfCategoriesId: List<String> = emptyList(),
    val listOfCategoriesRevenue: List<Double> = emptyList(),
    val listOfBrandsName: List<String> = emptyList(),
    val listOfBrandsId: List<String> = emptyList(),
    val expanded: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateBack: Boolean = false,
    val isEditMode: Boolean = false
)

@KoinViewModel
class AddProductViewModel(
    private val productUseCases: ProductUseCases,
    private val categoryUseCases: CategoryUseCases,
    private val scanBarcodeUseCase: ScanBarcodeUseCase,
    private val brandUseCases: BrandUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(productName = newName) }
    }

    /**
     * Calcula el Precio Promedio Ponderado (PPP) basándose en el inventario
     * existente y el nuevo stock que se está añadiendo.
     */
    private fun calculatePPP(
        addedCostStr: String,
        addedStockStr: String,
        existingStock: Double,
        existingPrice: Double
    ): Pair<Double, Double> {
        val addedCost = addedCostStr.toDoubleOrNull() ?: 0.0
        val addedStock = addedStockStr.toDoubleOrNull() ?: 0.0

        val finalStock = existingStock + addedStock
        val totalValue = (existingStock * existingPrice) + addedCost

        val newUnitPrice = if (finalStock > 0) totalValue / finalStock else 0.0
        return Pair(finalStock, newUnitPrice)
    }

    fun onPriceChange(newTotalCost: String) {
        if (newTotalCost.isEmpty() || newTotalCost.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { state ->
                val (finalStock, unitPrice) = calculatePPP(
                    addedCostStr = newTotalCost,
                    addedStockStr = state.productStock,
                    existingStock = state.existingStock,
                    existingPrice = state.existingPriceBuy
                )
                state.copy(
                    productTotalCost = newTotalCost,
                    calculatedUnitPrice = unitPrice,
                    finalCalculatedStock = finalStock
                )
            }
        }
    }

    fun onStockChange(newStock: String) {
        if (newStock.isEmpty() || newStock.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { state ->
                val (finalStock, unitPrice) = calculatePPP(
                    addedCostStr = state.productTotalCost,
                    addedStockStr = newStock,
                    existingStock = state.existingStock,
                    existingPrice = state.existingPriceBuy
                )
                state.copy(
                    productStock = newStock,
                    calculatedUnitPrice = unitPrice,
                    finalCalculatedStock = finalStock
                )
            }
        }
    }

    fun onCategoryChange(newCategoryName: String) {
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

    fun onBrandChange(newBrandName: String) {
        val index = _uiState.value.listOfBrandsName.indexOf(newBrandName)
        val brandId = if (index != -1) _uiState.value.listOfBrandsId[index] else ""

        _uiState.update {
            it.copy(
                productBrand = newBrandName,
                productBrandId = brandId
            )
        }
    }

    fun onCodeChanged(newCode: String) {
        _uiState.update { it.copy(productCode = newCode) }
    }

    init {
        listCategories()
        listBrands()
    }

    fun loadProduct(barcode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val product = productUseCases.findByCode(barcode).firstOrNull()
            if (product != null) {
                _uiState.update { state ->
                    val categoryIndex = state.listOfCategoriesId.indexOf(product.categoryId)
                    val categoryName = if (categoryIndex != -1) state.listOfCategoriesName[categoryIndex] else ""
                    val categoryRevenue = if (categoryIndex != -1) state.listOfCategoriesRevenue[categoryIndex] else 0.0
                    
                    val brandIndex = state.listOfBrandsId.indexOf(product.brandId)
                    val brandName = if (brandIndex != -1) state.listOfBrandsName[brandIndex] else ""

                    state.copy(
                        productId = product.productId,
                        productCode = product.barcode,
                        productName = product.name,
                        
                        // Para la edición, dejamos los campos vacíos para que el usuario 
                        // ingrese solo la cantidad "extra" que está comprando.
                        productTotalCost = "",
                        productStock = "",
                        
                        // Guardamos los valores existentes para calcular el PPP
                        existingStock = product.stockQuantity,
                        existingPriceBuy = product.priceBuy,
                        finalCalculatedStock = product.stockQuantity,
                        calculatedUnitPrice = product.priceBuy,
                        
                        productCategory = categoryName,
                        productCategoryId = product.categoryId,
                        productBrand = brandName,
                        productBrandId = product.brandId,
                        productRevenueCategory = categoryRevenue,
                        isEditMode = true,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, productCode = barcode) }
            }
        }
    }

    fun startScanning() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val scannedCode = scanBarcodeUseCase()
            if (scannedCode != null) {
                _uiState.update { it.copy(productCode = scannedCode, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun listCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoryUseCases.refreshCategories()
            try {
                categoryUseCases.getAll().collect { categories ->
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
                    it.copy(error = "Error: ${e.message}", isLoading = false)
                }
            }
        }
    }

    private fun listBrands() {
        viewModelScope.launch {
            brandUseCases.getAll().collect { brands ->
                _uiState.update { 
                    it.copy(
                        listOfBrandsName = brands.map { b -> b.name },
                        listOfBrandsId = brands.map { b -> b.brandId }
                    ) 
                }
            }
        }
    }

    fun saveProduct() {
        viewModelScope.launch {
            val state = _uiState.value
            
            if (state.productName.isBlank() || state.productCode.isBlank() || state.productCategoryId.isBlank()) {
                _uiState.update { it.copy(error = "Por favor, completa los campos obligatorios") }
                return@launch
            }
            
            // Si es un producto nuevo, el costo y el stock inicial no pueden estar vacíos
            if (!state.isEditMode && (state.productTotalCost.isBlank() || state.productStock.isBlank())) {
                _uiState.update { it.copy(error = "El costo total y el stock son obligatorios para un nuevo producto") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            
            // Tomamos el Costo Unitario calculado con PPP y el Stock final.
            val unitPurchasePrice = PriceUtils.roundPurchasePrice(state.calculatedUnitPrice)
            val finalStock = state.finalCalculatedStock
            
            val revenueCategory = state.productRevenueCategory

            val rawSellingPrice = unitPurchasePrice + (unitPurchasePrice * (revenueCategory/100))
            val finalSellingPrice = PriceUtils.roundSellingPrice(rawSellingPrice)
            val priceWithoutIGV = PriceUtils.calculatePriceExcludingIGV(finalSellingPrice)

            try {
                var brandId = state.productBrandId
                if (brandId.isBlank()) {
                    if (state.listOfBrandsId.isNotEmpty()) {
                        brandId = state.listOfBrandsId.first()
                    } else {
                        val defaultBrand = BrandModel(name = "General")
                        brandUseCases.save(defaultBrand)
                        brandId = defaultBrand.brandId
                    }
                }

                val product = ProductModel(
                    productId = state.productId ?: UUID.randomUUID().toString(),
                    name = state.productName,
                    brandId = brandId,
                    barcode = state.productCode,
                    priceBuy = unitPurchasePrice, 
                    priceSell = finalSellingPrice,
                    priceExcludingIGV = priceWithoutIGV,
                    stockQuantity = finalStock,
                    categoryId = state.productCategoryId
                )

                productUseCases.save(product)
                _uiState.update { it.copy(isLoading = false, navigateBack = true) }
            } catch (e: Exception) {
                Log.e("AddProductViewModel", "Error saving product", e)
                _uiState.update {
                    it.copy(error = "Error al guardar: ${e.message}", isLoading = false)
                }
            }
        }
    }

    fun onErrorShow() {
        _uiState.update { it.copy(error = null) }
    }
}