package com.pinao.panchitaapp.domain.usecase.client

import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.repository.ClientRepository

class DeleteClientUseCase(
    private val clientRepository: ClientRepository
) {
    suspend operator fun invoke(clientModel: ClientModel) {
        clientRepository.deleteClient(clientModel)
    }
}