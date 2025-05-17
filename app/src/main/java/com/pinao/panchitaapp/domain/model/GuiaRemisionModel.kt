package com.pinao.panchitaapp.domain.model

data class GuiaRemisionModel (
    val id_guia: Int = System.currentTimeMillis().hashCode(),
    val id_sender : Int = 0,
    val id_receiver : Int = 0,
    val date_guia : String = "",
    val num_guia : String = "",
)