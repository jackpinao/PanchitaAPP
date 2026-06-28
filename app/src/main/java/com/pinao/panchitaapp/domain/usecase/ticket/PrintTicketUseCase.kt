package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.service.BluetoothPrinterService

/**
 * Caso de uso responsable de coordinar la impresión del ticket térmico de una venta.
 */
class PrintTicketUseCase(
    private val printerService: BluetoothPrinterService
) {
    suspend operator fun invoke(
        deviceAddress: String,
        ticket: SaleModel,
        products: List<ProductModel>,
        clientName: String,
        clientDoc: String
    ): Result<Unit> {
        return printerService.printTicket(
            deviceAddress = deviceAddress,
            ticket = ticket,
            products = products,
            clientName = clientName,
            clientDoc = clientDoc
        )
    }
}
