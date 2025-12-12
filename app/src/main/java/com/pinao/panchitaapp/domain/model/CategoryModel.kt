package com.pinao.panchitaapp.domain.model

import java.util.UUID

class CategoryModel (
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val revenue: Double = 0.0,
)