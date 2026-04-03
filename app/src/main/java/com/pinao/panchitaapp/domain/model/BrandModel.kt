package com.pinao.panchitaapp.domain.model

import java.util.UUID

data class BrandModel(
    val brandId: String = UUID.randomUUID().toString(),
    val storeId: String = "",
    val name: String = "",
    var isSynced: Boolean = false
)
