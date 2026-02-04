package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.TicketModel

interface TicketRepository {
    suspend fun saveFullSale(ticket: TicketModel, products: List<ProductModel>)
}