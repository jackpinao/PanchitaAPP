package com.pinao.panchitaapp.domain.usecase.client

import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.repository.ClientRepository

class SaveClientUseCase(
    private val repository: ClientRepository
) {
    suspend operator fun invoke(clientModel: ClientModel) {
        repository.addClient(clientModel)
    }
}