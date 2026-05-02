package com.pinao.panchitaapp.presentation.common

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetCurrentDateTime {

    companion object {
        const val DB_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss"
        const val DB_FORMAT_SHORT = "yyyy-MM-dd"
        const val UI_FORMAT = "dd-MM-yyyy"
    }

    fun getCurrentDateTime(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val instant = Instant.ofEpochMilli(currentTimeMillis)
        val formatter = DateTimeFormatter.ofPattern(DB_FORMAT_FULL)
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    fun getCurrentDateTime2(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val instant = Instant.ofEpochMilli(currentTimeMillis)
        val formatter = DateTimeFormatter.ofPattern(DB_FORMAT_SHORT)
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    fun getCurrentDateTime3(dateTime: Long): String {
        val dt = addOneDay(dateTime)
        val instant = Instant.ofEpochMilli(dt)
        val formatter = DateTimeFormatter.ofPattern(DB_FORMAT_SHORT)
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    fun formatToDisplay(dateString: String): String {
        return try {
            val fullFormatter = DateTimeFormatter.ofPattern(DB_FORMAT_FULL)
                .withZone(ZoneId.systemDefault())
            val shortFormatter = DateTimeFormatter.ofPattern(DB_FORMAT_SHORT)
                .withZone(ZoneId.systemDefault())
            val displayFormatter = DateTimeFormatter.ofPattern(UI_FORMAT)
                .withZone(ZoneId.systemDefault())

            if (dateString.length > 10) {
                val temporal = fullFormatter.parse(dateString)
                displayFormatter.format(temporal)
            } else {
                val temporal = shortFormatter.parse(dateString)
                displayFormatter.format(temporal)
            }
        } catch (e: Exception) {
            dateString
        }
    }

    private fun addOneDay(dateTime: Long): Long {
        return dateTime + 86400000
    }

}