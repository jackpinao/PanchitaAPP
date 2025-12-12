package com.pinao.panchitaapp.domain.usecase.client

import com.pinao.panchitaapp.domain.repository.ClientRepository

class GetAllClientsUseCase(
    private val repository: ClientRepository
) {
    operator fun invoke() = repository.getClients()
}