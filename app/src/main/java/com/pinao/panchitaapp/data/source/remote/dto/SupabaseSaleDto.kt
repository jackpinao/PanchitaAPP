package com.pinao.panchitaapp.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseSaleDto(
    @SerialName("id") val id: String,
    @SerialName("tenant_id") val tenantId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("customer_id") val customerId: String?,
    @SerialName("sale_number") val saleNumber: String,
    @SerialName("subtotal") val subtotal: Double,
    @SerialName("discount") val discount: Double = 0.0,
    @SerialName("igv") val igv: Double = 0.0,
    @SerialName("total") val total: Double,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("amount_paid") val amountPaid: Double = 0.0,
    @SerialName("change_amount") val changeAmount: Double = 0.0,
    @SerialName("status") val status: String = "completed",
    @SerialName("offline_id") val offlineId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SupabaseSaleDetailDto(
    @SerialName("id") val id: String,
    @SerialName("tenant_id") val tenantId: String,
    @SerialName("sale_id") val saleId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("product_name") val productName: String,
    @SerialName("quantity") val quantity: Double,
    @SerialName("unit_price") val unitPrice: Double,
    @SerialName("discount") val discount: Double = 0.0,
    @SerialName("subtotal") val subtotal: Double,
    @SerialName("created_at") val createdAt: String? = null
)
