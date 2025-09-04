package com.pinao.panchitaapp.domain.model

import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import java.util.Date

data class ClientModel(
    val id: Int = System.currentTimeMillis().hashCode(),
    val name: String = "",
    val numDoc: String = "",
    val active: Boolean = true,
    val dateCreate: String = GetCurrentDateTime().getCurrentDateTime(),
)