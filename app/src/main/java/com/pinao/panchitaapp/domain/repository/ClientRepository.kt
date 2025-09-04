package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.ClientModel
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun getClients(): Flow<List<ClientModel>>
    suspend fun addClient(clientModel: ClientModel)
    suspend fun updateClient(clientModel: ClientModel)
    suspend fun deleteClient(clientModel: ClientModel)
}