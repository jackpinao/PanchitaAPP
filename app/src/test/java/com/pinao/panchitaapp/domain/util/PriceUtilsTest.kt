package com.pinao.panchitaapp.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PriceUtilsTest {

    @Test
    fun `roundSellingPrice should round down when decimal is less than 5`() {
        val result1 = PriceUtils.roundSellingPrice(1.01)
        val result2 = PriceUtils.roundSellingPrice(5.34)
        val result3 = PriceUtils.roundSellingPrice(9.94)

        assertEquals(1.0, result1, 0.0)
        assertEquals(5.3, result2, 0.0)
        assertEquals(9.9, result3, 0.0)
    }

    @Test
    fun `roundSellingPrice should round up when decimal is 5 or more`() {
        // Ahora sí podemos usar el 1.05 natural porque BigDecimal no pierde precisión
        val result1 = PriceUtils.roundSellingPrice(1.05)
        val result2 = PriceUtils.roundSellingPrice(5.38)
        val result3 = PriceUtils.roundSellingPrice(9.99)

        // El delta vuelve a ser 0.0 porque BigDecimal garantiza el valor matemáticamente perfecto
        assertEquals(1.1, result1, 0.0)
        assertEquals(5.4, result2, 0.0)
        assertEquals(10.0, result3, 0.0)
    }

    @Test
    fun `calculatePriceExcludingIGV should divide selling price by 1_18 exactly`() {
        val basePrice1 = PriceUtils.calculatePriceExcludingIGV(118.0)
        val basePrice2 = PriceUtils.calculatePriceExcludingIGV(59.0)

        // Verificamos que devuelve el precio sin IGV con solo 2 decimales exactos
        assertEquals(100.0, basePrice1, 0.0)
        assertEquals(50.0, basePrice2, 0.0)
    }

    @Test
    fun `calculatePriceExcludingIGV should return 0 when selling price is 0`() {
        val result = PriceUtils.calculatePriceExcludingIGV(0.0)
        assertEquals(0.0, result, 0.0)
    }

    // --- extractNetCost ---

    @Test
    fun `extractNetCost should return same value when no flags are active`() {
        val result = PriceUtils.extractNetCost(100.0, includesIgv = false, includesPercepcion = false)
        assertEquals(100.0, result, 0.0)
    }

    @Test
    fun `extractNetCost should divide by IGV factor when only includesIgv is true`() {
        // 118.0 / 1.18 = 100.0
        val result = PriceUtils.extractNetCost(118.0, includesIgv = true, includesPercepcion = false)
        assertEquals(100.0, result, 0.0001)
    }

    @Test
    fun `extractNetCost should divide by percepcion factor when only includesPercepcion is true`() {
        // 102.0 / 1.02 = 100.0
        val result = PriceUtils.extractNetCost(102.0, includesIgv = false, includesPercepcion = true)
        assertEquals(100.0, result, 0.0001)
    }

    @Test
    fun `extractNetCost should divide by combined factor when both flags are active`() {
        // 100 * 1.18 * 1.02 = 120.36 → 120.36 / (1.18 * 1.02) = 100.0
        val combined = 100.0 * 1.18 * 1.02
        val result = PriceUtils.extractNetCost(combined, includesIgv = true, includesPercepcion = true)
        assertEquals(100.0, result, 0.01)
    }

    @Test
    fun `extractNetCost should return 0 when totalCost is 0`() {
        val result = PriceUtils.extractNetCost(0.0, includesIgv = true, includesPercepcion = true)
        assertEquals(0.0, result, 0.0)
    }

    @Test
    fun `PERCEPCION_RATE constant should be 1_02`() {
        assertEquals(0, java.math.BigDecimal("1.02").compareTo(PriceUtils.PERCEPCION_RATE))
    }
}