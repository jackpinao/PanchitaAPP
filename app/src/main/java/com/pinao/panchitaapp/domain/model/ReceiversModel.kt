package com.pinao.panchitaapp.domain.model

data class ReceiversModel(
    val id_receiver: Int = System.currentTimeMillis().hashCode(),
    val name_receiver: String = "",
    val address_receiver: String = "",
    val phone_receiver: String = "",
    val email_receiver: String = "",
)
