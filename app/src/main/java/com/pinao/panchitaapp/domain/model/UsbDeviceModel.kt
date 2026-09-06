package com.pinao.panchitaapp.domain.model

/**
 * Modelo de dominio que representa un dispositivo USB conectado (impresora térmica).
 */
data class UsbDeviceModel(
    val name: String,
    val deviceId: Int,
    val vendorId: Int,
    val productId: Int
)
