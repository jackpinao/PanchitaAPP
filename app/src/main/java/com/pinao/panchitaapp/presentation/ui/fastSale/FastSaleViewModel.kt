package com.pinao.panchitaapp.presentation.ui.fastSale

import com.pinao.panchitaapp.data.source.local.SessionManager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.service.TicketPdfService
import com.pinao.panchitaapp.domain.usecase.Auth.EnsureCurrentUserUseCase
import com.pinao.panchitaapp.domain.usecase.client.SaveClientUseCase
import com.pinao.panchitaapp.domain.usecase.ticket.CompleteSaleUseCase
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.util.UUID

data class FastSaleItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val quantity: Double
)

data class FastSaleUiState(
    val items: List<FastSaleItem> = emptyList(),
    val productNameInput: String = "",
    val productPriceInput: String = "",
    val productQuantityInput: String = "1",
    val clientName: String = "",
    val clientDoc: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val downloadStatus: String? = null,
    val printingStatus: String? = null,
    val saleCompleted: Boolean = false
) {
    val total: Double
        get() = items.sumOf { it.price * it.quantity }
}

@KoinViewModel
class FastSaleViewModel(
    private val completeSaleUseCase: CompleteSaleUseCase,
    private val pdfService: TicketPdfService,
    private val saveClientUseCase: SaveClientUseCase,
    private val ensureCurrentUserUseCase: EnsureCurrentUserUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FastSaleUiState())
    val uiState: StateFlow<FastSaleUiState> = _uiState.asStateFlow()

    val sessionTicketId = System.currentTimeMillis().toString()

    fun onProductNameChange(name: String) {
        _uiState.update { it.copy(productNameInput = name) }
    }

    fun onProductPriceChange(price: String) {
        if (price.isEmpty() || price.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(productPriceInput = price) }
        }
    }

    fun onProductQuantityChange(quantity: String) {
        if (quantity.isEmpty() || quantity.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(productQuantityInput = quantity) }
        }
    }

    fun onClientNameChange(name: String) = _uiState.update { it.copy(clientName = name) }
    fun onClientDocChange(doc: String) = _uiState.update { it.copy(clientDoc = doc) }

    fun addItem() {
        val state = _uiState.value
        val name = state.productNameInput.trim()
        val price = state.productPriceInput.toDoubleOrNull() ?: 0.0
        val quantity = state.productQuantityInput.toDoubleOrNull() ?: 0.0

        if (name.isEmpty() || price <= 0 || quantity <= 0) {
            _uiState.update { it.copy(errorMessage = "Ingrese nombre, precio y cantidad válidos") }
            return
        }

        val newItem = FastSaleItem(name = name, price = price, quantity = quantity)
        _uiState.update { 
            it.copy(
                items = it.items + newItem,
                productNameInput = "",
                productPriceInput = "",
                productQuantityInput = "1",
                errorMessage = null
            )
        }
    }

    fun removeItem(item: FastSaleItem) {
        _uiState.update { state ->
            state.copy(items = state.items.filter { it.id != item.id })
        }
    }

    private fun mapItemsToProductModels(items: List<FastSaleItem>): List<ProductModel> {
        val storeId = sessionManager.getStoreId() ?: ""
        return items.map { item ->
            ProductModel(
                productId = UUID.randomUUID().toString(),
                storeId = storeId,
                name = item.name,
                priceSell = item.price,
                stockQuantity = item.quantity // Usado como cantidad para el ticket
            )
        }
    }

    fun finalizeSale() {
        val state = _uiState.value
        if (state.items.isEmpty()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val date = GetCurrentDateTime().getCurrentDateTime()
                val userId = ensureCurrentUserUseCase()
                
                if (state.clientName.isNotBlank() || state.clientDoc.isNotBlank()) {
                    saveClientUseCase(ClientModel(name = state.clientName, numDoc = state.clientDoc))
                }

                val storeId = sessionManager.getStoreId() ?: ""
                val ticket = SaleModel(
                    saleId = sessionTicketId,
                    storeId = storeId,
                    saleDate = date,
                    totalAmount = state.total,
                    isSynced = true,
                    userId = userId,
                    clientId = ""
                )

                val productModels = mapItemsToProductModels(state.items)
                completeSaleUseCase(ticket, productModels)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        saleCompleted = true,
                        downloadStatus = "Venta finalizada exitosamente"
                    )
                }
            } catch (e: Exception) {
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
            val storeId = sessionManager.getStoreId() ?: ""
            val ticket = SaleModel(
                saleId = sessionTicketId,
                storeId = storeId,
                saleDate = GetCurrentDateTime().getCurrentDateTime(),
                totalAmount = state.total
            )

            val productModels = mapItemsToProductModels(state.items)

            pdfService.generateAndSaveTicket(
                ticket = ticket,
                products = productModels,
                clientName = state.clientName,
                clientDoc = state.clientDoc
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        downloadStatus = "PDF guardado exitosamente"
                    )
                }
            }.onFailure { e ->
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

    fun resetSale() {
        _uiState.update { FastSaleUiState() }
    }

    fun clearErrorMessage() = _uiState.update { it.copy(errorMessage = null) }
    fun clearDownloadStatus() = _uiState.update { it.copy(downloadStatus = null) }
    fun clearPrintingStatus() = _uiState.update { it.copy(printingStatus = null) }
}
