package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class ProductModel(
    val id: String = UUID.randomUUID().toString(),
    val idCategory: String = "",
    val idDetailTicketEntity : String = "",
    val name: String = "",
    val description: String = "",
    val sellingPrice: Double = 0.0,
    val purchasePrice: Double = 0.0,
    val priceExcludingIGV: Double = 0.0,
    val stock: Double = 0.0,
    val code: String = "",
    val image: String = "",
)
