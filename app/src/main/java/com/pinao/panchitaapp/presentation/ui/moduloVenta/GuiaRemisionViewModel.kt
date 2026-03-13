package com.pinao.panchitaapp.presentation.ui.moduloVenta

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import com.pinao.panchitaapp.domain.service.TicketPdfService
import com.pinao.panchitaapp.domain.usecase.client.SaveClientUseCase
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.domain.usecase.products.ScanBarcodeUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.TemporaryProductUseCases
import com.pinao.panchitaapp.domain.usecase.ticket.CompleteSaleUseCase
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.util.UUID

/**
 * Estado único para la pantalla de Guía de Remisión.
 */
data class GuiaRemisionUiState(
    val products: List<ProductModel> = emptyList(),
    val clientName: String = "",
    val clientDoc: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val scannedProduct: ProductModel? = null,
    val quantity: String = "",
    val showAddDialog: Boolean = false,
    val showNotFoundError: Boolean = false,
    val showSelectionSheet: Boolean = false, // Nuevo estado para el diálogo de selección
    val lastScannedCode: String = "",
    val downloadStatus: String? = null,
    val printingStatus: String? = null,
    val isEditing: Boolean = false
)

/**
 * ViewModel encargado de la lógica de la Guía de Remisión.
 */
@KoinViewModel
class GuiaRemisionViewModel(
    private val productUseCases: ProductUseCases,
    private val saveClientUseCase: SaveClientUseCase,
    private val scanBarcodeUseCase: ScanBarcodeUseCase,
    private val completeSaleUseCase: CompleteSaleUseCase,
    private val pdfService: TicketPdfService,
    private val temporaryProductUseCases: TemporaryProductUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuiaRemisionUiState())
    val uiState: StateFlow<GuiaRemisionUiState> = _uiState.asStateFlow()

    val sessionTicketId = System.currentTimeMillis().toString()

    init {
        refreshAndObserveProducts()
    }

    private fun refreshAndObserveProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            launch {
                try {
                    productUseCases.refreshProducts()
                } catch (e: Exception) {
                    Log.e("GuiaRemisionViewModel", "Error refreshing remote products", e)
                }
            }

            temporaryProductUseCases.getAll()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = "Error al obtener productos: ${e.message}") }
                    Log.e("GuiaRemisionViewModel", "Error al obtener productos", e)
                }
                .collect { list ->
                    val productsForUi = list.map { temp ->
                        ProductModel(
                            productId = temp.productId,
                            name = temp.name,
                            barcode = temp.code,
                            priceSell = temp.price,
                            priceExcludingIGV = temp.priceExcludingIGV,
                            stockQuantity = temp.quantity,
                            detailTicketEntityId = temp.id
                        )
                    }
                    _uiState.update { it.copy(products = productsForUi, isLoading = false) }
                }
        }
    }

    fun clearErrorMessage() = _uiState.update { it.copy(errorMessage = null) }

    fun onScanClick() {
        _uiState.update { it.copy(showSelectionSheet = true) }
    }

    fun startScanningProduct() {
        _uiState.update { it.copy(showSelectionSheet = false) }
        viewModelScope.launch {
            val code = scanBarcodeUseCase() ?: return@launch
            handleProductByCode(code)
        }
    }

    fun handleProductByCode(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(lastScannedCode = code) }
            productUseCases.findByCode(code).firstOrNull()?.let { product ->
                _uiState.update {
                    it.copy(
                        scannedProduct = product,
                        showAddDialog = true,
                        isEditing = false
                    )
                }
            } ?: run {
                _uiState.update { it.copy(showNotFoundError = true) }
            }
        }
    }

    /**
     * Prepara el diálogo para editar un producto ya existente en la lista.
     */
    fun onProductLongClick(product: ProductModel) {
        _uiState.update {
            it.copy(
                scannedProduct = product,
                quantity = product.stockQuantity.toString(),
                showAddDialog = true,
                isEditing = true
            )
        }
    }

    fun onQuantityChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(quantity = value) }
        }
    }

    /**
     * Decide si guardar nuevo o actualizar según el flag isEditing.
     */
    fun onConfirmQuantity() {
        if (_uiState.value.isEditing) {
            updateProductQuantity()
        } else {
            addScannedProductToList()
        }
    }

    private fun addScannedProductToList() {
        val product = _uiState.value.scannedProduct ?: return
        val qty = _uiState.value.quantity.toDoubleOrNull() ?: 0.0

        if (qty <= 0) return
        if (qty > product.stockQuantity) {
            _uiState.update { it.copy(errorMessage = "Stock insuficiente. Disponible: ${product.stockQuantity}") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val updatedCatalogProduct =
                    product.copy(stockQuantity = product.stockQuantity - qty)
                productUseCases.save(updatedCatalogProduct)

                val tempItem = TemporaryProductModel(
                    id = UUID.randomUUID().toString(),
                    productId = product.productId,
                    name = product.name,
                    code = product.barcode,
                    price = product.priceSell,
                    priceExcludingIGV = product.priceExcludingIGV,
                    quantity = qty
                )
                temporaryProductUseCases.save(tempItem)
                closeDialogs()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al actualizar stock: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun updateProductQuantity() {
        val productInGui = _uiState.value.scannedProduct ?: return
        val newQty = _uiState.value.quantity.toDoubleOrNull() ?: 0.0
        val oldQty =
            productInGui.stockQuantity // En esta UI, stock representa la cantidad en la guía

        if (newQty <= 0) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // 1. Obtener el producto real del catálogo para ver el stock actual
                val catalogProduct = productUseCases.findByCode(productInGui.barcode).firstOrNull()
                    ?: throw Exception("Producto no encontrado en catálogo")

                val diff = newQty - oldQty

                // 2. Si pedimos más, verificar disponibilidad
                if (diff > 0 && catalogProduct.stockQuantity < diff) {
                    _uiState.update { it.copy(errorMessage = "No hay suficiente stock adicional. Disponible: ${catalogProduct.stockQuantity}") }
                    return@launch
                }

                // 3. Actualizar catálogo: Si diff es positivo, resta; si es negativo (devolución), suma.
                val updatedCatalogProduct =
                    catalogProduct.copy(stockQuantity = catalogProduct.stockQuantity - diff)
                productUseCases.save(updatedCatalogProduct)

                // 4. Actualizar tabla temporal
                val tempItem = TemporaryProductModel(
                    id = productInGui.detailTicketEntityId,
                    productId = productInGui.productId,
                    name = productInGui.name,
                    code = productInGui.barcode,
                    price = productInGui.priceSell,
                    priceExcludingIGV = productInGui.priceExcludingIGV,
                    quantity = newQty
                )
                temporaryProductUseCases.save(tempItem)

                closeDialogs()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al actualizar: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onClientNameChange(name: String) = _uiState.update { it.copy(clientName = name) }
    fun onClientDocChange(doc: String) = _uiState.update { it.copy(clientDoc = doc) }

    fun removeItem(product: ProductModel) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val originalProduct = productUseCases.findByCode(product.barcode).firstOrNull()
                if (originalProduct != null) {
                    val restoredProduct =
                        originalProduct.copy(stockQuantity = originalProduct.stockQuantity + product.stockQuantity)
                    productUseCases.save(restoredProduct)
                }
                temporaryProductUseCases.delete(
                    TemporaryProductModel(
                        id = product.detailTicketEntityId,
                        productId = product.productId,
                        name = product.name,
                        code = product.barcode,
                        price = product.priceSell,
                        priceExcludingIGV = product.priceExcludingIGV,
                        quantity = product.stockQuantity
                    )
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al devolver stock: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun finalizeSale() {
        val state = _uiState.value
        if (state.products.isEmpty()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val date = GetCurrentDateTime().getCurrentDateTime()
                saveClientUseCase(ClientModel(name = state.clientName, numDoc = state.clientDoc))

                val ticket = SaleModel(
                    saleId = sessionTicketId,
                    saleDate = date,
                    totalAmount = state.products.sumOf { it.priceSell * it.stockQuantity },
                    isSynced = true,
                    userId = "",
                    clientId = ""
                )

                completeSaleUseCase(ticket, state.products)
                temporaryProductUseCases.clearAll()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        downloadStatus = "Venta finalizada y stock actualizado"
                    )
                }
                Log.d("GuiaRemisionViewModel", "Venta y Stock sincronizados globalmente")

            } catch (e: Exception) {
                Log.e("GuiaRemisionViewModel", "Error al finalizar venta", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun downloadTicket() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val ticket = SaleModel(
                saleId = sessionTicketId,
                saleDate = GetCurrentDateTime().getCurrentDateTime(),
                totalAmount = state.products.sumOf { it.priceSell * it.stockQuantity }
            )

            pdfService.generateAndSaveTicket(ticket, state.products)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            downloadStatus = "PDF guardado exitosamente"
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error PDF: ${e.message}"
                        )
                    }
                }
        }
    }

    fun initiatePrintTicket() {
        _uiState.update { it.copy(printingStatus = "Iniciando impresión...") }
        // Aquí iría la lógica real de impresión
    }

    fun closeDialogs() {
        _uiState.update {
            it.copy(
                showAddDialog = false,
                showNotFoundError = false,
                showSelectionSheet = false,
                scannedProduct = null,
                quantity = "",
                isEditing = false
            )
        }
    }

    fun clearDownloadStatus() = _uiState.update { it.copy(downloadStatus = null) }
    fun clearPrintingStatus() = _uiState.update { it.copy(printingStatus = null) }
}