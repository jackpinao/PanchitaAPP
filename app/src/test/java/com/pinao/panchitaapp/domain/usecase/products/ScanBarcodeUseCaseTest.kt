package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.BarcodeScanner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScanBarcodeUseCaseTest {

    private lateinit var useCase: ScanBarcodeUseCase
    private val barcodeScanner: BarcodeScanner = mockk()

    @Before
    fun setup() {
        useCase = ScanBarcodeUseCase(barcodeScanner)
    }

    @Test
    fun `invoke should return barcode from scanner on success`() = runTest {
        val expectedBarcode = "123456789"
        coEvery { barcodeScanner.startScan() } returns expectedBarcode

        val result = useCase()

        assertEquals(expectedBarcode, result)
        coVerify(exactly = 1) { barcodeScanner.startScan() }
    }

    @Test
    fun `invoke should return null if scanner fails or cancels`() = runTest {
        coEvery { barcodeScanner.startScan() } returns null

        val result = useCase()

        assertNull(result)
        coVerify(exactly = 1) { barcodeScanner.startScan() }
    }
}