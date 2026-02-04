package com.pinao.panchitaapp.domain.model

import java.util.UUID

class TicketModel(
    val id: String = UUID.randomUUID().toString(),
    val date: String = "",
    val total: Double = 0.0,
    val state: String = "",
    val idUser: String = "",
    val idClient: String = "",
)
