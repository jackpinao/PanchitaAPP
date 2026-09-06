package com.pinao.panchitaapp.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.service.BluetoothPrinterService
import com.pinao.panchitaapp.domain.service.UsbPrinterService
import com.pinao.panchitaapp.domain.usecase.ticket.PrintTicketUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel que gestiona la lógica de la pantalla de Ajustes.
 */
@KoinViewModel
class SettingsViewModel(
    private val sessionManager: SessionManager,
    private val bluetoothPrinterService: BluetoothPrinterService,
    private val usbPrinterService: UsbPrinterService,
    private val printTicketUseCase: PrintTicketUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        val name = sessionManager.getStoreName() ?: ""
        val ruc = sessionManager.getStoreRuc() ?: ""
        val address = sessionManager.getStoreAddress() ?: ""
        val phone = sessionManager.getStorePhone() ?: ""
        val connectionType = sessionManager.getPrinterConnectionType()
        val printerAddress = sessionManager.getPrinterAddress()
        val isBtEnabled = bluetoothPrinterService.isBluetoothEnabled()

        _uiState.update {
            it.copy(
                storeName = name,
                storeRuc = ruc,
                storeAddress = address,
                storePhone = phone,
                printerConnectionType = connectionType,
                selectedPrinterAddress = printerAddress,
                isBluetoothEnabled = isBtEnabled
            )
        }

        refreshPrinters()
    }

    fun setPrinterConnectionType(type: String) {
        sessionManager.savePrinterConnectionType(type)
        _uiState.update {
            it.copy(
                printerConnectionType = type,
                selectedPrinterAddress = sessionManager.getPrinterAddress()
            )
        }
        refreshPrinters()
    }

    fun refreshPrinters() {
        val connectionType = sessionManager.getPrinterConnectionType()
        if (connectionType == "USB") {
            val usbDevices = usbPrinterService.getConnectedPrinters()
            _uiState.update {
                it.copy(connectedUsbDevices = usbDevices)
            }
        } else {
            val isBtEnabled = bluetoothPrinterService.isBluetoothEnabled()
            val btDevices = if (isBtEnabled) {
                bluetoothPrinterService.getPairedPrinters()
            } else {
                emptyList()
            }
            _uiState.update {
                it.copy(
                    isBluetoothEnabled = isBtEnabled,
                    pairedDevices = btDevices
                )
            }
        }
    }

    fun selectPrinter(addressOrId: String) {
        sessionManager.savePrinterAddress(addressOrId)
        _uiState.update {
            it.copy(
                selectedPrinterAddress = addressOrId,
                successMessage = "Impresora predeterminada guardada"
            )
        }

        // Si es USB, solicitar permiso si no se tiene
        if (_uiState.value.printerConnectionType == "USB") {
            val deviceId = addressOrId.toIntOrNull()
            if (deviceId != null && !usbPrinterService.hasPermission(deviceId)) {
                usbPrinterService.requestPermission(deviceId)
            }
        }
    }

    fun onStoreNameChange(value: String) {
        _uiState.update { it.copy(storeName = value) }
    }

    fun onStoreRucChange(value: String) {
        _uiState.update { it.copy(storeRuc = value) }
    }

    fun onStoreAddressChange(value: String) {
        _uiState.update { it.copy(storeAddress = value) }
    }

    fun onStorePhoneChange(value: String) {
        _uiState.update { it.copy(storePhone = value) }
    }

    fun saveStoreDetails() {
        val state = _uiState.value
        if (state.storeName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre del negocio es obligatorio") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }
        try {
            sessionManager.saveStoreDetails(
                name = state.storeName.trim(),
                ruc = state.storeRuc.trim(),
                address = state.storeAddress.trim(),
                phone = state.storePhone.trim()
            )
            _uiState.update {
                it.copy(
                    isSaving = false,
                    successMessage = "Datos del negocio guardados correctamente"
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isSaving = false,
                    errorMessage = "Error al guardar: ${e.message}"
                )
            }
        }
    }

    fun printTestTicket() {
        val state = _uiState.value
        val address = state.selectedPrinterAddress
        if (address.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "No se ha seleccionado ninguna impresora") }
            return
        }

        _uiState.update { it.copy(isTestingPrint = true) }
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            val testSale = SaleModel(
                saleId = "TEST-0001",
                saleDate = dateStr,
                totalAmount = 25.50
            )

            val testProducts = listOf(
                ProductModel(
                    productId = "TEST-P1",
                    name = "Producto Demo 1",
                    priceSell = 10.00,
                    stockQuantity = 2.0
                ),
                ProductModel(
                    productId = "TEST-P2",
                    name = "Producto Demo 2",
                    priceSell = 5.50,
                    stockQuantity = 1.0
                )
            )

            val result = printTicketUseCase(
                deviceAddress = address,
                ticket = testSale,
                products = testProducts,
                clientName = "Cliente de Prueba",
                clientDoc = "12345678"
            )

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isTestingPrint = false,
                        successMessage = "Ticket de prueba impreso correctamente"
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isTestingPrint = false,
                        errorMessage = "Error al imprimir: ${exception.message}"
                    )
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
