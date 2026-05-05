package com.pinao.panchitaapp.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseCategoryDto(
    @SerialName("id") val id: String = "",
    @SerialName("tenant_id") val tenantId: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("description") val description: String? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
