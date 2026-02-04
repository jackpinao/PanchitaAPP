package com.pinao.panchitaapp.domain.usecase.ticket

data class DetailTicketUseCases(
    val save : SaveDetailTicketUseCase,
    val getDetailsByTicketId: GetDetailsByTicketIdUseCase
)