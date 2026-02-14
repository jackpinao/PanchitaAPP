package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.SaleDetailModel
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository

class SaveDetailTicketUseCase(private val repository: DetailTicketRepository) {
    suspend operator fun invoke(details: SaleDetailModel) =
        repository.saveTicketDetails(details)
}