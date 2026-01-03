package com.pinao.panchitaapp.domain.repository

/**
 * Interfaz que define el contrato para el escáner de códigos de barras.
 * Pertenece a la capa de Dominio, por lo que es agnóstica de la implementación (GMS, ZXing, etc.).
 */
interface BarcodeScanner {
    /**
     * Inicia el escaneo y suspende hasta obtener un resultado.
     * @return El código escaneado como String, o null si el usuario canceló o hubo un error manejado.
     */
    suspend fun startScan(): String?
}