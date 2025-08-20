package com.pinao.panchitaapp.domain.model

data class SendersModel(
    val id_sender: Int = System.currentTimeMillis().hashCode(),
    val name_sender: String = "",
    val address_sender: String = "",
    val phone_sender: String = "",
    val email_sender: String = "",
)
