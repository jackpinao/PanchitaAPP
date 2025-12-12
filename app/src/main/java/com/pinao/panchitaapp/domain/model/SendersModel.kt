package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class SendersModel(
    val id_sender: String = UUID.randomUUID().toString(),
    val name_sender: String = "",
    val address_sender: String = "",
    val phone_sender: String = "",
    val email_sender: String = "",
)
