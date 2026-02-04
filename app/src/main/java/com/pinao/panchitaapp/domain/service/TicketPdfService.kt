package com.pinao.panchitaapp.domain.service

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.TicketModel

/**
 * Interfaz para la generación de documentos PDF.
 * Ubicada en Domain para mantener el ViewModel agnóstico a la implementación de Android.
 */
interface TicketPdfService {
    /**
     * Genera y guarda un PDF basado en la información de la venta.
     * @return Result con el estado de la operación.
     */
    suspend fun generateAndSaveTicket(ticket: TicketModel, products: List<ProductModel>): Result<Unit>
}