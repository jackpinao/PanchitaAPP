package com.pinao.panchitaapp.utils

class Common {
    fun String.sinAcento(): String {
        val original = "ÁÉÍÓÚÜáéíóúü"
        val replacement = "AEIOUUaeiouu"
        val output = StringBuilder()
        for (c in this) {
            val index = original.indexOf(c)
            if (index >= 0) {
                output.append(replacement[index])
            } else {
                output.append(c)
            }
        }
        return output.toString()
    }
}