package com.pinao.panchitaapp.presentation.common

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetCurrentDateTime {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val instant = Instant.ofEpochMilli(currentTimeMillis)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime2(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val instant = Instant.ofEpochMilli(currentTimeMillis)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime3(dateTime: Long): String {
        println(dateTime)
        val dt = addOneDay(dateTime)
        println(dt)
        val instant = Instant.ofEpochMilli(dt)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    private fun addOneDay(dateTime: Long): Long {
        return dateTime + 86400000
    }

}