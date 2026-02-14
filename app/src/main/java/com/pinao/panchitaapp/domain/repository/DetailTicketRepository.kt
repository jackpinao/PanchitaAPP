package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.SaleDetailModel
import kotlinx.coroutines.flow.Flow

interface DetailTicketRepository {
    /**
     * Guarda una lista de detalles de ticket en la base de datos local.
     */
    suspend fun saveTicketDetails(details: SaleDetailModel)

    /**
     * Obtiene los detalles vinculados a un ticket específico.
     */
    fun getDetailsByTicketId(ticketId: String): Flow<List<SaleDetailModel>>
}
