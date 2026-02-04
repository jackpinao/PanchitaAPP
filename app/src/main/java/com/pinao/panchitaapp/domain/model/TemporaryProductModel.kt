package com.pinao.panchitaapp.domain.model

/**
 * Modelo de dominio para representar un ítem en el borrador de la guía.
 */
data class TemporaryProductModel(
    val id: String,         // ID único del ítem en el borrador
    val productId: String,  // Referencia al ID real en el catálogo
    val name: String,
    val code: String,
    val price: Double,
    val priceExcludingIGV: Double,
    val quantity: Double
)