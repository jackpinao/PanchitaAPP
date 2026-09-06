package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.repository.PrinterSettingsRepository
import com.pinao.panchitaapp.domain.service.BluetoothPrinterService
import com.pinao.panchitaapp.domain.service.UsbPrinterService

/**
 * Caso de uso responsable de coordinar la impresión del ticket térmico de una venta.
 * Soporta conexiones Bluetooth y USB OTG.
 */
class PrintTicketUseCase(
    private val bluetoothPrinterService: BluetoothPrinterService,
    private val usbPrinterService: UsbPrinterService,
    private val printerSettingsRepository: PrinterSettingsRepository
) {
    suspend operator fun invoke(
        deviceAddress: String,
        ticket: SaleModel,
        products: List<ProductModel>,
        clientName: String,
        clientDoc: String
    ): Result<Unit> {
        val connectionType = printerSettingsRepository.getPrinterConnectionType()
        return if (connectionType == "USB") {
            val deviceId = deviceAddress.toIntOrNull()
                ?: return Result.failure(Exception("ID de dispositivo USB no válido."))
            usbPrinterService.printTicket(
                deviceId = deviceId,
                ticket = ticket,
                products = products,
                clientName = clientName,
                clientDoc = clientDoc
            )
        } else {
            bluetoothPrinterService.printTicket(
                deviceAddress = deviceAddress,
                ticket = ticket,
                products = products,
                clientName = clientName,
                clientDoc = clientDoc
            )
        }
    }
}
