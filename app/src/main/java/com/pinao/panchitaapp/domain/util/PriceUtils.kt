package com.pinao.panchitaapp.domain.util

import kotlin.math.round

object PriceUtils {
    private const val IGV_FACTOR = 1.18

    /**
     * Redondea el precio al décimo más cercano.
     * Ejemplo: 1.01 a 1.04 -> 1.0
     * Ejemplo: 1.05 a 1.09 -> 1.1
     */
    fun roundSellingPrice(price: Double): Double {
        return round(price * 10) / 10.0
    }

    /**
     * Calcula el precio base (sin IGV) a partir del precio de venta final.
     * Fórmula: Precio con IGV / 1.18
     */
    fun calculatePriceExcludingIGV(sellingPrice: Double): Double {
        return sellingPrice / IGV_FACTOR
    }
}
