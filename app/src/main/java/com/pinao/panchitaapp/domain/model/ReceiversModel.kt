package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class ReceiversModel(
    val id_receiver: String = UUID.randomUUID().toString(),
    val name_receiver: String = "",
    val address_receiver: String = "",
    val phone_receiver: String = "",
    val email_receiver: String = "",
)
