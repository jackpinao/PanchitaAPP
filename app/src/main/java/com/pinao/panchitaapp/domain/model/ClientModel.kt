package com.pinao.panchitaapp.domain.model

import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import java.util.Date
import java.util.UUID

data class ClientModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val numDoc: String = "",
    val active: Boolean = true,
    val dateCreate: String = GetCurrentDateTime().getCurrentDateTime(),
)