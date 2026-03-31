package com.pinao.panchitaapp.presentation.common

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyExtensionsTest {

    @Test
    fun `toCurrency should format valid double to Peruvian Soles correctly`() {
        // Arrange
        val amount = 1500.50

        // Act
        val result = amount.toCurrency()

        // Assert
        // Nota: Dependiendo de la versión de Java/Android local, el separador de miles y el símbolo
        // pueden tener ligeras variaciones (como espacios irrompibles). 
        // Usualmente es "S/ 1,500.50" o "S/1,500.50" o "S/ 1500.50".
        // Lo importante es que contenga el símbolo y el valor correcto.
        assert(result.contains("S/"))
        assert(result.contains("1500.50") || result.contains("1,500.50") || result.contains("1.500,50"))
    }

    @Test
    fun `toCurrency should format null to zero Peruvian Soles`() {
        // Arrange
        val amount: Double? = null

        // Act
        val result = amount.toCurrency()

        // Assert
        assert(result.contains("S/"))
        assert(result.contains("0.00") || result.contains("0,00"))
    }

    @Test
    fun `formatDecimal should format valid double to two decimal string`() {
        // Arrange
        val amount = 15.5

        // Act
        val result = amount.formatDecimal()

        // Assert
        // El separador decimal puede ser punto o coma dependiendo del Locale de la máquina que corre el test.
        assert(result == "15.50" || result == "15,50")
    }

    @Test
    fun `formatDecimal should format long decimals correctly rounded`() {
        // Arrange
        val amount = 10.126

        // Act
        val result = amount.formatDecimal()

        // Assert
        assert(result == "10.13" || result == "10,13")
    }

    @Test
    fun `formatDecimal should format null to zero with two decimals`() {
        // Arrange
        val amount: Double? = null

        // Act
        val result = amount.formatDecimal()

        // Assert
        assert(result == "0.00" || result == "0,00")
    }
}