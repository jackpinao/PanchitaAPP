package com.pinao.panchitaapp.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class SaleResponseDto(
    @SerializedName("message") val message: String,
    @SerializedName("sale") val sale: SaleResultDto? = null
)

data class SaleResultDto(
    @SerializedName("id") val id: String,
    @SerializedName("tenant_id") val tenantId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("customer_id") val customerId: String?,
    @SerializedName("sale_number") val saleNumber: String,
    @SerializedName("total") val total: Double
)
