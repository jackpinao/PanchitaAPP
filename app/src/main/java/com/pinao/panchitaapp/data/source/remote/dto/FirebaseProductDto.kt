package com.pinao.panchitaapp.data.source.remote.dto

data class FirebaseProductDto(
    val productId: String = "",
    val storeId: String = "",
    val categoryId: String = "",
    val brandId: String = "",
    val name: String = "",
    val description: String = "",
    val priceBuy: Double = 0.0,
    val priceSell: Double = 0.0,
    val priceExcludingIGV: Double = 0.0,
    val stockQuantity: Double = 0.0,
    val stockMin: Double = 5.0,
    val barcode: String = "",
    val image: String = "",
    val lastUpdated: String = "",
    val isDeleted: Boolean = false
)
