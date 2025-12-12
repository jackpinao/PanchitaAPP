package com.pinao.panchitaapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithCategory(
    @Embedded
    val product: ProductsEntity,
    @Relation(
        parentColumn = "idCategory", // Campo de la entidad principal (ProductsEntity)
        entityColumn = "id"         // Campo de la entidad relacionada (CategoryEntity)
    )
    val category: CategoryEntity
)