package com.pinao.panchitaapp.domain.service

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.model.UsbDeviceModel

/**
 * Interfaz de servicio de dominio para interactuar con la impresora USB térmica.
 */
interface UsbPrinterService {
    /**
     * Obtiene la lista de dispositivos USB conectados reconocidos como impresoras o dispositivos bulk OUT.
     */
    fun getConnectedPrinters(): List<UsbDeviceModel>

    /**
     * Verifica si se tiene permiso concedido para acceder al dispositivo USB.
     */
    fun hasPermission(deviceId: Int): Boolean

    /**
     * Solicita permiso al usuario mediante el sistema de Android para acceder al dispositivo USB.
     */
    fun requestPermission(deviceId: Int)

    /**
     * Envía la información de la venta a imprimir en la impresora USB especificada.
     */
    suspend fun printTicket(
        deviceId: Int,
        ticket: SaleModel,
        products: List<ProductModel>,
        clientName: String,
        clientDoc: String
    ): Result<Unit>
}
