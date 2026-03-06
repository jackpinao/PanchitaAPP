package com.pinao.panchitaapp.presentation.common

import java.text.NumberFormat
import java.util.Locale

/**
 * Convierte un Double a un formato de moneda local.
 * Por defecto usa el Locale de Perú (es-PE) para mostrar "S/."
 */
fun Double?.toCurrency(): String {
    val amount = this ?: 0.0
    val locale = Locale.forLanguageTag("es-PE")
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(amount)
}

/**
 * Formatea un Double a string con 2 decimales sin símbolo de moneda.
 */
fun Double?.formatDecimal(): String {
    val amount = this ?: 0.0
    return String.format(Locale.getDefault(), "%.2f", amount)
}
