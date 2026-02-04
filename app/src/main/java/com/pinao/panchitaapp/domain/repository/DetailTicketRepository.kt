package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.DetailTicketModel
import kotlinx.coroutines.flow.Flow

interface DetailTicketRepository {
    /**
     * Guarda una lista de detalles de ticket en la base de datos local.
     */
    suspend fun saveTicketDetails(details: DetailTicketModel)

    /**
     * Obtiene los detalles vinculados a un ticket específico.
     */
    fun getDetailsByTicketId(ticketId: String): Flow<List<DetailTicketModel>>
}
