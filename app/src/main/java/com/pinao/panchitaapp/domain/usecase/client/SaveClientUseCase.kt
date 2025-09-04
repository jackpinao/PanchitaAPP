package com.pinao.panchitaapp.domain.usecase.client

import com.pinao.panchitaapp.data.repository.ClientRepositoryImpl
import com.pinao.panchitaapp.domain.model.ClientModel

class SaveClientUseCase(
    private val repository: ClientRepositoryImpl
) {
    suspend operator fun invoke(clientModel: ClientModel) {
        repository.addClient(clientModel)
    }
}