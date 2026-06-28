package com.pinao.panchitaapp.domain.service

import com.pinao.panchitaapp.domain.model.BluetoothDeviceModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel

/**
 * Interfaz de servicio de dominio para interactuar con la impresora Bluetooth térmica.
 * Permite que los ViewModels permanezcan agnósticos a las APIs directas de Android.
 */
interface BluetoothPrinterService {
    /**
     * Verifica si el adaptador Bluetooth del dispositivo está habilitado.
     */
    fun isBluetoothEnabled(): Boolean

    /**
     * Obtiene la lista de dispositivos Bluetooth emparejados (paired) en el sistema.
     */
    fun getPairedPrinters(): List<BluetoothDeviceModel>

    /**
     * Envía la información de la venta a imprimir en la dirección física de la impresora.
     * @return Result.success(Unit) si se imprimió correctamente, o Result.failure con la excepción correspondiente.
     */
    suspend fun printTicket(
        deviceAddress: String,
        ticket: SaleModel,
        products: List<ProductModel>,
        clientName: String,
        clientDoc: String
    ): Result<Unit>
}
