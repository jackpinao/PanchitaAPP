package com.pinao.panchitaapp.domain.model

import java.util.UUID

class SaleModel(
    val saleId: String = UUID.randomUUID().toString(),
    val storeId: String = "",
    val userId: String = "",
    val clientId: String = "",
    val saleDate: String = "",
    val totalAmount: Double = 0.0,
    val paymentType: String = "",
    val isSynced: Boolean = false,
)
