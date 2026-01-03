package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.BarcodeScanner

class ScanBarcodeUseCase(
    private val barcodeScanner: BarcodeScanner
) {
    suspend operator fun invoke(): String? {
        return barcodeScanner.startScan()
    }
}