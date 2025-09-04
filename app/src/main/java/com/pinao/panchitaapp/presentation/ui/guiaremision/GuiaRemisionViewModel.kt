package com.pinao.panchitaapp.presentation.ui.guiaremision

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.usecase.client.SaveClientUseCase
import com.pinao.panchitaapp.domain.usecase.products.DeleteProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.FindCodeProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.GetAllProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.SaveProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class GuiaRemisionViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val findCodeProductUseCase: FindCodeProductUseCase,
    private val saveProductsUseCase: SaveProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val saveClientUseCase: SaveClientUseCase,
) : ViewModel() {

    private val _guiaRemisionUiState =
        MutableStateFlow<GuiaRemisionUiState>(GuiaRemisionUiState.Loading)
    val guiaRemisionUiState: StateFlow<GuiaRemisionUiState> = _guiaRemisionUiState.asStateFlow()
    private val _productsUiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val productsUiState: StateFlow<ProductsUiState> = _productsUiState.asStateFlow()

    // Datos de los productos
    val code = System.currentTimeMillis().toString()
    private val _codeProduct = MutableStateFlow<String>(code)
    val codeProduct: StateFlow<String> = _codeProduct.asStateFlow()
    private val _nameProduct = MutableStateFlow<String>("")
    val nameProduct: StateFlow<String> = _nameProduct.asStateFlow()
    private val _priceProduct = MutableStateFlow<String>("")
    val priceProduct: StateFlow<String> = _priceProduct.asStateFlow()
    private val _quantityProduct = MutableStateFlow<String>("")
    val quantityProduct: StateFlow<String> = _quantityProduct.asStateFlow()

    //Datos de los clientes
    private val _nameClient = MutableStateFlow<String>("")
    val nameClient: StateFlow<String> = _nameClient.asStateFlow()
    private val _numDocClient = MutableStateFlow<String>("")
    val numDocClient: StateFlow<String> = _numDocClient.asStateFlow()

    private val _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    init {
        //downLoadGuiaRemision()
        downLoadProducts()
    }

    private fun downLoadProducts() {
        viewModelScope.launch {
            getAllProductsUseCase()
                .onStart { _productsUiState.value = ProductsUiState.Loading }
                .catch { exception -> _productsUiState.value = ProductsUiState.Error(exception) }
                .collect { products ->
                    _productsUiState.value = ProductsUiState.Success(products)
                    Log.d("GuiaRemisionViewModel", "Productos descargados: $products")
                }
        }
    }

//    private fun downLoadGuiaRemision() {
//        TODO("Not yet implemented")
//    }

    fun onCodeProductChange(newValue: String) {
        Log.d("GuiaRemisionViewModel", "onCodeProductChange: $newValue")
        _codeProduct.value = newValue
    }

    fun onNameClientChange(newValue: String) {
        _nameClient.value = newValue
    }
    fun onNumDocClientChange(newValue: String) {
        _numDocClient.value = newValue
    }

    fun onNameProductChange(newValue: String) {
        _nameProduct.value = newValue
    }

    fun onPriceProductChange(newValue: String) {
        _priceProduct.value = newValue
    }

    fun onQuantityProductChange(newValue: String) {
        _quantityProduct.value = newValue
    }

    fun checkCodeProduct(codeProduct: String): Boolean {
        Log.d("GuiaRemisionViewModel", "checkCodeProduct: $codeProduct")
        var isProduct = false
        viewModelScope.launch {
            findCodeProductUseCase(codeProduct)
                .onStart { _productsUiState.value = ProductsUiState.Loading }
                .catch { exception -> _productsUiState.value = ProductsUiState.Error(exception) }
                .collect { product ->
                    if (product != null) {
                        _productsUiState.value = ProductsUiState.Success(listOf(product))
                        Log.d("GuiaRemisionViewModel", "Producto encontrado: $product")
                        isProduct = true
                    } else {
                        Log.d("GuiaRemisionViewModel", "Producto no encontrado")
                        isProduct = false
                    }
                }
        }
        return isProduct
    }

    fun updateProduct(productModel: ProductModel) {
        viewModelScope.launch {
            try {
                _productsUiState.value = ProductsUiState.Loading
                saveProductsUseCase(productModel)
                _productsUiState.value = ProductsUiState.Success(listOf(productModel))
                //downLoadProducts()
            } catch (e: Exception) {
                Log.e("GuiaRemisionViewModel", "Error al actualizar el producto", e)
                _productsUiState.value = ProductsUiState.Error(e)
            }

        }
    }

    fun onItemRemove(productModel: ProductModel) {
        viewModelScope.launch {
            deleteProductUseCase(productModel)
            downLoadProducts()
        }
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onDialogClose() {
        _showDialog.value = false
    }
}