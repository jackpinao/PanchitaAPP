package com.pinao.panchitaapp.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseProductDto(
    @SerialName("id") val id: String = "",
    @SerialName("tenant_id") val tenantId: String = "",
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("name") val name: String = "",
    @SerialName("description") val description: String? = null,
    @SerialName("barcode") val barcode: String? = null,
    @SerialName("sku") val sku: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("purchase_price") val purchasePrice: Double = 0.0,
    @SerialName("sale_price") val salePrice: Double = 0.0,
    @SerialName("revenue") val revenue: Double = 0.0,
    @SerialName("current_stock") val currentStock: Double = 0.0,
    @SerialName("min_stock") val minStock: Double = 0.0,
    @SerialName("unit") val unit: String = "UND",
    @SerialName("expiry_date") val expiryDate: String? = null,
    @SerialName("taxable") val taxable: Boolean = true,
    @SerialName("is_weighed") val isWeighed: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

