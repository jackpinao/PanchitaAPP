package com.pinao.panchitaapp.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GmsBarcodeScannerImplTest {

    private lateinit var scannerImpl: GmsBarcodeScannerImpl
    private val mockContext: Context = mockk()
    private val mockScanner: GmsBarcodeScanner = mockk()
    private val mockBarcode: Barcode = mockk()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        
        // Mockeamos el método estático de GmsBarcodeScanning que obtiene el cliente del escaner
        mockkStatic(GmsBarcodeScanning::class)
        every { GmsBarcodeScanning.getClient(mockContext, any()) } returns mockScanner

        scannerImpl = GmsBarcodeScannerImpl(mockContext)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkStatic(GmsBarcodeScanning::class)
    }

    @Test
    fun `startScan should return barcode rawValue on success`() = runTest {
        // Arrange
        val expectedCode = "7751234567890"
        every { mockBarcode.rawValue } returns expectedCode
        
        // Simulamos que el escaner de Google devuelve un barcode exitoso
        every { mockScanner.startScan() } returns Tasks.forResult(mockBarcode)

        // Act
        val result = scannerImpl.startScan()

        // Assert
        assertEquals(expectedCode, result)
    }

    @Test
    fun `startScan should return null on failure or cancellation`() = runTest {
        // Arrange
        // Simulamos que el usuario cerró el escaner o hubo un error (Task lanza excepción)
        val exception = Exception("User cancelled scan")
        every { mockScanner.startScan() } returns Tasks.forException(exception)

        // Act
        val result = scannerImpl.startScan()

        // Assert
        assertNull(result)
    }
}