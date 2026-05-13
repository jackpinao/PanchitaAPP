package com.pinao.panchitaapp.utils

class Common {
    fun String.sinAcento(): String {
        val output = StringBuilder(length)
        for (c in this) {
            output.append(
                when (c) {
                    'Á' -> 'A'
                    'É' -> 'E'
                    'Í' -> 'I'
                    'Ó' -> 'O'
                    'Ú', 'Ü' -> 'U'
                    'á' -> 'a'
                    'é' -> 'e'
                    'í' -> 'i'
                    'ó' -> 'o'
                    'ú', 'ü' -> 'u'
                    else -> c
                }
            )
        }
        return output.toString()
    }
}