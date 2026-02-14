package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.repository.SaleRepository

/**
 * Caso de uso para finalizar una venta completa.
 * Coordina el guardado del ticket y sus detalles en una sola transacción.
 */
class CompleteSaleUseCase(private val repository: SaleRepository) {
    suspend operator fun invoke(ticket: SaleModel, products: List<ProductModel>) {
        repository.saveFullSale(ticket, products)
    }
}