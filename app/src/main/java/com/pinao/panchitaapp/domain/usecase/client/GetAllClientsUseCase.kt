package com.pinao.panchitaapp.domain.usecase.client

import com.pinao.panchitaapp.data.repository.ClientRepositoryImpl

class GetAllClientsUseCase(
    private val repository: ClientRepositoryImpl
) {
    operator fun invoke() = repository.getClients()
}