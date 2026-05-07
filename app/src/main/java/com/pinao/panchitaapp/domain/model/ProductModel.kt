package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class ProductModel(
    val productId: String = UUID.randomUUID().toString(),
    val storeId: String = "",
    val categoryId: String = "",
    val detailTicketEntityId : String = "",
    val name: String = "",
    val description: String = "",
    val priceBuy: Double = 0.0,
    val priceSell: Double = 0.0,
    val priceExcludingIGV: Double = 0.0,
    val revenue: Double = 0.0,
    val stockQuantity: Double = 0.0,
    val stockMin: Double = 5.0,
    val barcode: String = "",
    val image: String = "",
    val lastUpdated: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
