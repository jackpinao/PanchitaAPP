package com.pinao.panchitaapp.domain.usecase.client

import com.pinao.panchitaapp.data.repository.ClientRepositoryImpl
import com.pinao.panchitaapp.domain.model.ClientModel

class DeleteClientUseCase(
    private val clientRepository: ClientRepositoryImpl
) {
    suspend operator fun invoke(clientModel: ClientModel){
        clientRepository.deleteClient(clientModel)
    }
}