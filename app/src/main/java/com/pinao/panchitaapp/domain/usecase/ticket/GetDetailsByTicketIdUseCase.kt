package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.repository.DetailTicketRepository

class GetDetailsByTicketIdUseCase(private val repository: DetailTicketRepository) {
    operator fun invoke(ticketId: String) =
        repository.getDetailsByTicketId(ticketId)

}