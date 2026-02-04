package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.DetailTicketModel
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository

class SaveDetailTicketUseCase(private val repository: DetailTicketRepository) {
    suspend operator fun invoke(details: DetailTicketModel) =
        repository.saveTicketDetails(details)
}