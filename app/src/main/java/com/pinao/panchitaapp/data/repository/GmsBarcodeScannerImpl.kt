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
                .build()

            val scanner = GmsBarcodeScanning.getClient(context, options)

            // Verificamos e instalamos el módulo si es necesario (crítico para Android 13+)
            val moduleInstall = com.google.android.gms.common.moduleinstall.ModuleInstall.getClient(context)
            val moduleInstallRequest = com.google.android.gms.common.moduleinstall.ModuleInstallRequest.newBuilder()
                .addApi(scanner)
                .build()
            
            // Esperamos a que se asegure la instalación del módulo
            moduleInstall.installModules(moduleInstallRequest).await()

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
