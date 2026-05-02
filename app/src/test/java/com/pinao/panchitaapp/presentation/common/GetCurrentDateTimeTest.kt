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
        // Debe tener el formato: yyyy-MM-dd HH:mm:ss
        assertEquals(19, result.length)
        
        // Validamos usando Regex que coincida con el patrón de dígitos
        val regex = Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")
        assertTrue("El formato devuelto no coincide con yyyy-MM-dd HH:mm:ss: $result", result.matches(regex))
    }

    @Test
    fun `getCurrentDateTime2 should return current date only in correct format`() {
        // Arrange
        val result = getCurrentDateTime.getCurrentDateTime2()

        // Assert
        // Debe tener el formato: yyyy-MM-dd
        assertEquals(10, result.length)

        val regex = Regex("\\d{4}-\\d{2}-\\d{2}")
        assertTrue("El formato devuelto no coincide con yyyy-MM-dd: $result", result.matches(regex))
    }

    @Test
    fun `getCurrentDateTime3 should add one day to given timestamp and format it`() {
        // Arrange
        val timestamp = 1672574400000L
        
        val expectedInstant = Instant.ofEpochMilli(timestamp + 86400000)
        val expectedFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
        val expectedString = expectedFormatter.format(expectedInstant)

        // Act
        val result = getCurrentDateTime.getCurrentDateTime3(timestamp)

        // Assert
        assertEquals(expectedString, result)
    }

    @Test
    fun `formatToDisplay should convert DB format to UI format`() {
        // Arrange
        val fullDbDate = "2026-05-02 15:30:45"
        val shortDbDate = "2026-05-02"
        val expectedDisplay = "02-05-2026"

        // Act
        val resultFull = getCurrentDateTime.formatToDisplay(fullDbDate)
        val resultShort = getCurrentDateTime.formatToDisplay(shortDbDate)

        // Assert
        assertEquals(expectedDisplay, resultFull)
        assertEquals(expectedDisplay, resultShort)
    }

    @Test
    fun `formatToDisplay should return original string if format is unknown`() {
        // Arrange
        val oldFormat = "02-05-2026 15:30:45"

        // Act
        val result = getCurrentDateTime.formatToDisplay(oldFormat)

        // Assert
        assertEquals(oldFormat, result)
    }
}