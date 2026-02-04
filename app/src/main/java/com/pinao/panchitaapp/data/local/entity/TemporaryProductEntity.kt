package com.pinao.panchitaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para almacenar temporalmente los productos de una Guía de Remisión en curso.
 */
@Entity(tableName = "temporary_product")
data class TemporaryProductEntity(
    @PrimaryKey
    val id: String, // ID único para el ítem en la tabla temporal
    val productId: String, // ID de referencia al producto original
    val name: String,
    val price: Double,
    val priceExcludingIGV: Double,
    val quantity: Double,
    val code: String
)