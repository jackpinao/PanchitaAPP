package com.pinao.panchitaapp.presentation.ui.settings

import com.pinao.panchitaapp.domain.model.BluetoothDeviceModel
import com.pinao.panchitaapp.domain.model.UsbDeviceModel

/**
 * Representa el estado de la UI para la pantalla de Configuración (Settings).
 */
data class SettingsUiState(
    val storeName: String = "",
    val storeRuc: String = "",
    val storeAddress: String = "",
    val storePhone: String = "",
    val printerConnectionType: String = "BLUETOOTH", // "BLUETOOTH" o "USB"
    val pairedDevices: List<BluetoothDeviceModel> = emptyList(),
    val connectedUsbDevices: List<UsbDeviceModel> = emptyList(),
    val selectedPrinterAddress: String? = null,
    val isBluetoothEnabled: Boolean = false,
    val isSaving: Boolean = false,
    val isTestingPrint: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
