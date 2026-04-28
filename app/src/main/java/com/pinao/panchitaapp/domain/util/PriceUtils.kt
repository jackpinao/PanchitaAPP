package com.pinao.panchitaapp.domain.util

import java.math.BigDecimal
import java.math.RoundingMode

object PriceUtils {
    // Usamos String en el constructor de BigDecimal para no perder precisión desde el inicio
    private val IGV_FACTOR = BigDecimal("1.18")

    /**
     * Tasa de percepción vigente (2%). Definida como constante para facilitar ajuste futuro.
     * Para cambiar la tasa, modificar solo este valor.
     */
    val PERCEPCION_RATE: BigDecimal = BigDecimal("1.02")

    /**
     * Redondea el precio al décimo más cercano usando BigDecimal para máxima precisión.
     * Ejemplo: 1.01 a 1.04 -> 1.0
     * Ejemplo: 1.05 a 1.09 -> 1.1
     */
    fun roundSellingPrice(price: Double): Double {
        if (price.isNaN() || price.isInfinite()) return 0.0
        val bdPrice = BigDecimal(price.toString())
        // setScale(1) significa 1 decimal. HALF_UP es el redondeo comercial estándar (>= 5 sube).
        return bdPrice.setScale(1, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * Redondea el precio de compra a 2 decimales para mostrar y guardar correctamente.
     */
    fun roundPurchasePrice(price: Double): Double {
        if (price.isNaN() || price.isInfinite()) return 0.0
        val bdPrice = BigDecimal(price.toString())
        return bdPrice.setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * Calcula el precio base (sin IGV) a partir del precio de venta final.
     * Fórmula: Precio con IGV / 1.18
     */
    fun calculatePriceExcludingIGV(sellingPrice: Double): Double {
        if (sellingPrice == 0.0 || sellingPrice.isNaN() || sellingPrice.isInfinite()) return 0.0
        
        val bdPrice = BigDecimal(sellingPrice.toString())
        // Dividimos limitando a 2 decimales exactos
        return bdPrice.divide(IGV_FACTOR, 2, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * Extrae el costo neto de compra descontando los impuestos incluidos en el precio.
     *
     * Si [includesIgv] es true, el precio ya incluye IGV (18%).
     * Si [includesPercepcion] es true, el precio ya incluye percepción ([PERCEPCION_RATE]).
     * Ambos flags son independientes y sus factores se multiplican.
     *
     * Ejemplo: totalCost=118.0, includesIgv=true → costoNeto=100.0
     * Ejemplo: totalCost=120.36, includesIgv=true, includesPercepcion=true → costoNeto=100.0
     */
    fun extractNetCost(
        totalCost: Double,
        includesIgv: Boolean,
        includesPercepcion: Boolean
    ): Double {
        if (totalCost == 0.0 || totalCost.isNaN() || totalCost.isInfinite()) return 0.0
        if (!includesIgv && !includesPercepcion) return totalCost

        var factor = BigDecimal.ONE
        if (includesIgv) factor = factor.multiply(IGV_FACTOR)
        if (includesPercepcion) factor = factor.multiply(PERCEPCION_RATE)

        val bdCost = BigDecimal(totalCost.toString())
        return bdCost.divide(factor, 4, RoundingMode.HALF_UP).toDouble()
    }
}
