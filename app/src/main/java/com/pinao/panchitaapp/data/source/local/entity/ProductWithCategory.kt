package com.pinao.panchitaapp.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithCategory(
    @Embedded
    val product: ProductsEntity,
    @Relation(
        parentColumn = "category_id", // Campo de la entidad principal (ProductsEntity)
        entityColumn = "category_id"         // Campo de la entidad relacionada (CategoryEntity)
    )
    val category: CategoryEntity
)