package com.pinao.panchitaapp.domain.model

data class SupplierModel(
    val id: String,
    val storeId: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val isSynced: Boolean = false
)