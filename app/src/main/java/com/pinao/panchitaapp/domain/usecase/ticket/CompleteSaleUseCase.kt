package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.DetailTicketModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.TicketModel
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import com.pinao.panchitaapp.domain.repository.TicketRepository

/**
 * Caso de uso para finalizar una venta completa.
 * Coordina el guardado del ticket y sus detalles en una sola transacción.
 */
class CompleteSaleUseCase(private val repository: TicketRepository) {
    suspend operator fun invoke(ticket: TicketModel, products: List<ProductModel>) {
        repository.saveFullSale(ticket, products)
    }
}