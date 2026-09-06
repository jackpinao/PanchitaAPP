package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.repository.PrinterSettingsRepository
import com.pinao.panchitaapp.domain.service.BluetoothPrinterService
import com.pinao.panchitaapp.domain.service.UsbPrinterService
import io.mockk.every
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrintTicketUseCaseTest {
    private lateinit var useCase: PrintTicketUseCase
    private val printerService: BluetoothPrinterService = mockk()
    private val usbPrinterService: UsbPrinterService = mockk()
    private val printerSettingsRepository: PrinterSettingsRepository = mockk()

    @Before
    fun setup() {
        every { printerSettingsRepository.getPrinterConnectionType() } returns "BLUETOOTH"
        useCase = PrintTicketUseCase(printerService, usbPrinterService, printerSettingsRepository)
    }


    @Test
    fun `invoke should call printTicket on printerService and return success`() = runTest {
        val ticket = SaleModel(saleId = "t1", totalAmount = 100.0)
        val products = listOf(
            ProductModel(productId = "p1", name = "Producto A", stockQuantity = 2.0)
        )
        val clientName = "Juan Perez"
        val clientDoc = "12345678"
        val deviceAddress = "00:11:22:33:44:55"

        coEvery {
            printerService.printTicket(any(), any(), any(), any(), any())
        } returns Result.success(Unit)

        val result = useCase(deviceAddress, ticket, products, clientName, clientDoc)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            printerService.printTicket(
                match { it == deviceAddress },
                withArg { assertEquals("t1", it.saleId) },
                withArg {
                    assertEquals(1, it.size)
                    assertEquals("p1", it[0].productId)
                },
                match { it == clientName },
                match { it == clientDoc }
            )
        }
    }

    @Test
    fun `invoke should return failure when printerService fails`() = runTest {
        val ticket = SaleModel(saleId = "t1", totalAmount = 100.0)
        val products = emptyList<ProductModel>()
        val clientName = ""
        val clientDoc = ""
        val deviceAddress = "00:11:22:33:44:55"
        val exception = Exception("Connection error")

        coEvery {
            printerService.printTicket(any(), any(), any(), any(), any())
        } returns Result.failure(exception)

        val result = useCase(deviceAddress, ticket, products, clientName, clientDoc)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
