package com.pinao.panchitaapp.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val databaseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Convierte una fecha de formato visual (DD/MM/YYYY) a formato de base de datos (YYYY-MM-DD).
     * Retorna la misma cadena si no coincide con el formato esperado.
     */
    fun toDatabaseFormat(displayDate: String?): String? {
        if (displayDate.isNullOrBlank()) return null
        return try {
            val date = displayFormat.parse(displayDate)
            date?.let { databaseFormat.format(it) } ?: displayDate
        } catch (e: Exception) {
            displayDate
        }
    }

    /**
     * Convierte una fecha de formato de base de datos (YYYY-MM-DD) a formato visual (DD/MM/YYYY).
     */
    fun toDisplayFormat(databaseDate: String?): String? {
        if (databaseDate.isNullOrBlank()) return null
        return try {
            val date = databaseFormat.parse(databaseDate)
            date?.let { displayFormat.format(it) } ?: databaseDate
        } catch (e: Exception) {
            databaseDate
        }
    }
}
