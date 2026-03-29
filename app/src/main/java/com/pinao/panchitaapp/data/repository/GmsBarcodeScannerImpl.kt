package com.pinao.panchitaapp.data.repository

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.pinao.panchitaapp.domain.repository.BarcodeScanner
import kotlinx.coroutines.tasks.await

/**
 * Implementación concreta del escáner usando Google Play Services (ML Kit).
 */
class GmsBarcodeScannerImpl(
    private val context: Context
) : BarcodeScanner {

    override suspend fun startScan(): String? {
        return try {
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .enableAutoZoom()
                // allowManualInput() comentado temporalmente para evitar SecurityException (Unknown calling package name)
                // .allowManualInput()
                .build()

            val scanner = GmsBarcodeScanning.getClient(context, options)

            // Convertimos la API basada en Tasks de Google a Corrutinas de Kotlin
            val barcode = scanner.startScan().await()
            barcode.rawValue
        } catch (e: Exception) {
            // Manejamos cancelaciones o errores devolviendo null
            Log.e("GmsBarcodeScannerImpl", "Error al escanear el código de barras", e)
            e.printStackTrace()
            null
        }
    }
}
