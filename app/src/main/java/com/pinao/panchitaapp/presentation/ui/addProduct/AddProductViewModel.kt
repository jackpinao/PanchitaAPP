package com.pinao.panchitaapp.presentation.ui.addProduct

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddProductViewModel(
    private val productUseCases: ProductUseCases
) : ViewModel() {

    private val _scannedText = MutableStateFlow("")
    val scannedText = _scannedText.asStateFlow()

    fun onTextChanged(newText: String) {
        _scannedText.value = newText
    }

    fun startScanning(context: Context) {
        // Implement your scanning logic here
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_CODE_128
            ).build()

        val scanner = GmsBarcodeScanning.getClient(context, options)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                barcode.rawValue?.let { code ->
                    _scannedText.value = code
                }
            }
            .addOnCanceledListener {
                Log.d("Scanner", "Escaneo cancelado por el usuario")
            }
            .addOnFailureListener { e ->

                Log.e("Scanner", "Error al escanear el código de barras", e)
                _scannedText.value = "Error: no se pudo escanear"
            }

    }
}
