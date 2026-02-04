package com.pinao.panchitaapp.domain.model

import java.util.UUID

class DetailTicketModel (
    val id: String = UUID.randomUUID().toString(),
    val date: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val amount: Int = 0,
    val import: Double = 0.0,
    val idTicket: String = "",
    val idProduct: String = ""
)