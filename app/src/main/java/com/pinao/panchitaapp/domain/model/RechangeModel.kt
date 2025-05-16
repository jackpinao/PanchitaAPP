package com.pinao.panchitaapp.domain.model

data class RechangeModel(
    val id: Int = System.currentTimeMillis().hashCode(),
    val date: String = "",
    val amount: Int = 0,
    val numPhone: String = "",
)
