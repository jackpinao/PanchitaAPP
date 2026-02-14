package com.pinao.panchitaapp.domain.model

import java.util.UUID

class SaleDetailModel (
    val saleDetailId: String = UUID.randomUUID().toString(),
    val saleId: String = "",
    val productId: String = "",
    val priceAtSale: Double = 0.0,
    val quantity: Double = 0.0,
    val subtotal: Double = 0.0,
)