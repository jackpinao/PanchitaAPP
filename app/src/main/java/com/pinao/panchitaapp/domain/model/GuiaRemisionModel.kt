package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class GuiaRemisionModel (
    val id_guia: String = UUID.randomUUID().toString(),
    val id_sender : Int = 0,
    val id_receiver : Int = 0,
    val date_guia : String = "",
    val num_guia : String = "",
)