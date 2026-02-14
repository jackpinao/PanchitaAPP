package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class CategoryModel (
    val categoryId: String = UUID.randomUUID().toString(),
    val storeId: String = "",
    val name: String = "",
    val revenue: Double = 0.0,
    val isSynced: Boolean = false
)