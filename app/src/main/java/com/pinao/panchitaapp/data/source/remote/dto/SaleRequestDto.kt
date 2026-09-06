package com.pinao.panchitaapp.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class SaleRequestDto(
    @SerializedName("items") val items: List<SaleItemRequestDto>,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("customerId") val customerId: String? = null,
    @SerializedName("amountPaid") val amountPaid: Double? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("offlineId") val offlineId: String? = null
)

data class SaleItemRequestDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("discount") val discount: Double = 0.0
)
