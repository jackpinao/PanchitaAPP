package com.pinao.panchitaapp.presentation.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetCurrentDateTimeTest {

    private lateinit var getCurrentDateTime: GetCurrentDateTime

    @Before
    fun setup() {
        getCurrentDateTime = GetCurrentDateTime()
    }

    @Test
    fun `getCurrentDateTime should return current date and time in correct format`() {
        // Arrange
        val result = getCurrentDateTime.getCurrentDateTime()

        // Assert
        // Debe tener el formato: dd-MM-yyyy HH:mm:ss (ej. 25-10-2023 15:30:45)
        // La longitud es exactamente 19 caracteres
        assertEquals(19, result.length)
        
        // Validamos usando Regex que coincida con el patrón de dígitos
        val regex = Regex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}")
        assertTrue("El formato devuelto no coincide con dd-MM-yyyy HH:mm:ss: $result", result.matches(regex))
    }

    @Test
    fun `getCurrentDateTime2 should return current date only in correct format`() {
        // Arrange
        val result = getCurrentDateTime.getCurrentDateTime2()

        // Assert
        // Debe tener el formato: dd-MM-yyyy (ej. 25-10-2023)
        assertEquals(10, result.length)

        val regex = Regex("\\d{2}-\\d{2}-\\d{4}")
        assertTrue("El formato devuelto no coincide con dd-MM-yyyy: $result", result.matches(regex))
    }

    @Test
    fun `getCurrentDateTime3 should add one day to given timestamp and format it`() {
        // Arrange
        // Epoch time para 1 de Enero de 2023 a las 12:00 UTC (1672574400000L)
        val timestamp = 1672574400000L
        
        // Sumamos un día (+86400000 ms) simulando la funcion interna addOneDay
        val expectedInstant = Instant.ofEpochMilli(timestamp + 86400000)
        val expectedFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault())
        val expectedString = expectedFormatter.format(expectedInstant)

        // Act
        val result = getCurrentDateTime.getCurrentDateTime3(timestamp)

        // Assert
        assertEquals(expectedString, result)
    }
}