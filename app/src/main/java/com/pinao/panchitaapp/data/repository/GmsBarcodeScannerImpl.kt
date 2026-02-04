package com.pinao.panchitaapp.data.repository

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.pinao.panchitaapp.domain.repository.BarcodeScanner
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementación concreta del escáner usando Google Play Services (ML Kit).
 */
class GmsBarcodeScannerImpl(
    private val context: Context
) : BarcodeScanner {

    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
//            Barcode.FORMAT_QR_CODE,
//            Barcode.FORMAT_AZTEC,
//            Barcode.FORMAT_CODE_128
        )
        .enableAutoZoom()
        .allowManualInput()
        .build()

    private val scanner = GmsBarcodeScanning.getClient(context, options)

    override suspend fun startScan(): String? {
        return try {
            // Convertimos la API basada en Tasks de Google a Corrutinas de Kotlin
            val barcode = scanner.startScan().await()
            barcode.rawValue
        } catch (e: Exception) {
            // Manejamos cancelaciones o errores devolviendo null
            // O podrías lanzar una excepción personalizada si prefieres manejar errores específicos
            Log.e("GmsBarcodeScannerImpl", "Error al escanear el código de barras", e)
            e.printStackTrace()
            null
        }
    }
}