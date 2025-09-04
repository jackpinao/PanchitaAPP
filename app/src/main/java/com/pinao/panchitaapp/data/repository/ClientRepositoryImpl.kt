package com.pinao.panchitaapp.data.repository

import com.pinao.panchitaapp.data.local.dao.ClientDao
import com.pinao.panchitaapp.data.mapper.ClientMapper
import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClientRepositoryImpl(
    private val clientDao: ClientDao
) : ClientRepository {
    override fun getClients(): Flow<List<ClientModel>> {
        return clientDao.getClients().map { items ->
            items.map { clientDao ->
                ClientMapper.toDomain(clientDao)
            }
        }
    }

    override suspend fun addClient(clientModel: ClientModel) {
        clientDao.insert(ClientMapper.toEntity(clientModel))
    }

    override suspend fun updateClient(clientModel: ClientModel) {
        clientDao.update(ClientMapper.toEntity(clientModel))
    }

    override suspend fun deleteClient(clientModel: ClientModel) {
        return clientDao.delete(ClientMapper.toEntity(clientModel))
    }
}