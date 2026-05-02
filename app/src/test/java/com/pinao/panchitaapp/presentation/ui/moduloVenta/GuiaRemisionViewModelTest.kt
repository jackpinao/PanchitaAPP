package com.pinao.panchitaapp.presentation.ui.moduloVenta

import android.util.Log
import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import com.pinao.panchitaapp.domain.service.TicketPdfService
import com.pinao.panchitaapp.domain.usecase.Auth.EnsureCurrentUserUseCase
import com.pinao.panchitaapp.domain.usecase.client.SaveClientUseCase
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.domain.usecase.products.ScanBarcodeUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.TemporaryProductUseCases
import com.pinao.panchitaapp.domain.usecase.ticket.CompleteSaleUseCase
import com.pinao.panchitaapp.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GuiaRemisionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val productUseCases: ProductUseCases = mockk(relaxed = true)
    private val saveClientUseCase: SaveClientUseCase = mockk(relaxed = true)
    private val scanBarcodeUseCase: ScanBarcodeUseCase = mockk()
    private val completeSaleUseCase: CompleteSaleUseCase = mockk(relaxed = true)
    private val pdfService: TicketPdfService = mockk(relaxed = true)
    private val temporaryProductUseCases: TemporaryProductUseCases = mockk(relaxed = true)
    private val ensureCurrentUserUseCase: EnsureCurrentUserUseCase = mockk(relaxed = true)

    private lateinit var viewModel: GuiaRemisionViewModel

    private val mockTemporaryProducts = listOf(
        TemporaryProductModel(
            id = "temp1",
            productId = "prod1",
            name = "Coca-Cola",
            code = "123",
            price = 2.5,
            priceExcludingIGV = 2.1,
            quantity = 2.0
        )
    )

    private val catalogProduct = ProductModel(
        productId = "prod1",
        name = "Coca-Cola",
        barcode = "123",
        priceSell = 2.5,
        priceExcludingIGV = 2.1,
        stockQuantity = 50.0
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        coEvery { productUseCases.refreshProducts() } returns Unit
        every { temporaryProductUseCases.getAll() } returns flowOf(mockTemporaryProducts)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `init should load temporary products into state`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val loadedState = awaitItem()

            assertFalse(loadedState.isLoading)
            assertEquals(1, loadedState.products.size)
            assertEquals("Coca-Cola", loadedState.products[0].name)

            coVerify { productUseCases.refreshProducts() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startScanningProduct with valid code should update state to showAddDialog`() = runTest {
        coEvery { scanBarcodeUseCase() } returns "123"
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem()

            viewModel.startScanningProduct()

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val resultState = expectMostRecentItem()

            assertTrue(resultState.showAddDialog)
            assertNotNull(resultState.scannedProduct)
            assertEquals("Coca-Cola", resultState.scannedProduct?.name)
            assertFalse(resultState.isEditing)
            assertEquals("123", resultState.lastScannedCode)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startScanningProduct with invalid code should showNotFoundError`() = runTest {
        coEvery { scanBarcodeUseCase() } returns "999"
        every { productUseCases.findByCode("999") } returns flowOf(null)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.startScanningProduct()

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = expectMostRecentItem()
            assertTrue(errorState.showNotFoundError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onQuantityChange should update quantity only with valid numbers`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onQuantityChange("5")
            val state1 = awaitItem()
            assertEquals("5", state1.quantity)

            viewModel.onQuantityChange("abc")
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmQuantity should add product to temporary list if not editing`() = runTest {
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.handleProductByCode("123")

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            val dialogState = expectMostRecentItem()
            assertTrue(dialogState.showAddDialog)

            viewModel.onQuantityChange("10")
            val stateConCantidad = awaitItem()
            assertEquals("10", stateConCantidad.quantity)

            viewModel.onConfirmQuantity()

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val finalState = expectMostRecentItem()
            assertFalse(finalState.showAddDialog)
            assertFalse(finalState.isLoading)

            coVerify { productUseCases.save(match { it.stockQuantity == 40.0 }) }
            coVerify { temporaryProductUseCases.save(match { it.quantity == 10.0 }) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmQuantity should update product quantity if editing`() = runTest {
        val productInGui = catalogProduct.copy(stockQuantity = 2.0)
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onProductLongClick(productInGui)

            val editingState = awaitItem()
            assertTrue(editingState.showAddDialog)
            assertTrue(editingState.isEditing)
            assertEquals("2.0", editingState.quantity)

            viewModel.onQuantityChange("5.0")
            awaitItem()

            viewModel.onConfirmQuantity()

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            val finalState = expectMostRecentItem()

            assertFalse(finalState.showAddDialog)
            assertFalse(finalState.isEditing)

            // Original era 50, en guia tenia 2, cambia a 5 -> pide 3 mas. Queda 47.
            coVerify { productUseCases.save(match { it.stockQuantity == 47.0 }) }
            coVerify { temporaryProductUseCases.save(match { it.quantity == 5.0 }) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmQuantity should fail if product is not found when editing`() = runTest {
        val productInGui = catalogProduct.copy(stockQuantity = 2.0)

        // Simulamos que el producto ya no existe en la base de datos maestra
        every { productUseCases.findByCode("123") } returns flowOf(null)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onProductLongClick(productInGui)
            awaitItem()

            viewModel.onQuantityChange("5.0")
            awaitItem()

            viewModel.onConfirmQuantity()

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            val finalState = expectMostRecentItem()

            assertEquals(
                "Error al actualizar: Producto no encontrado en catálogo",
                finalState.errorMessage
            )
            assertFalse(finalState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmQuantity should show error if stock is insufficient when adding`() = runTest {
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.handleProductByCode("123")

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            viewModel.onQuantityChange("100") // Mayor que los 50 disponibles en catálogo
            awaitItem()

            viewModel.onConfirmQuantity()

            val errorState = awaitItem()
            assertEquals("Stock insuficiente. Disponible: 50.0", errorState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmQuantity should show error if stock is insufficient when editing`() = runTest {
        val productInGui = catalogProduct.copy(stockQuantity = 2.0)
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct) // Solo quedan 50

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onProductLongClick(productInGui)
            awaitItem()

            // Editamos pidiendo 100, la diferencia seria 98, pero solo hay 50
            viewModel.onQuantityChange("100")
            awaitItem()

            viewModel.onConfirmQuantity()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = expectMostRecentItem()
            assertEquals(
                "No hay suficiente stock adicional. Disponible: 50.0",
                errorState.errorMessage
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeItem should restore stock and delete from temporary`() = runTest {
        val productToRemove = catalogProduct.copy(stockQuantity = 5.0) // 5 agregados en la guía
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct) // 50 en catálogo real

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.removeItem(productToRemove)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val finalState = expectMostRecentItem()
            assertFalse(finalState.isLoading)

            // Devuelve los 5 al catálogo, debe guardarse con 55
            coVerify { productUseCases.save(match { it.stockQuantity == 55.0 }) }
            coVerify { temporaryProductUseCases.delete(any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeItem should throw error if fails`() = runTest {
        val productToRemove = catalogProduct.copy(stockQuantity = 5.0)
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct)

        // Forzamos un fallo en Room / Firestore
        coEvery { productUseCases.save(any()) } throws Exception("Database locked")

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.removeItem(productToRemove)
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = expectMostRecentItem()
            assertEquals("Error al devolver stock: Database locked", errorState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClientNameChange and onClientDocChange should update state`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onClientNameChange("Juan Perez")
            val stateName = awaitItem()
            assertEquals("Juan Perez", stateName.clientName)

            viewModel.onClientDocChange("12345678")
            val stateDoc = awaitItem()
            assertEquals("12345678", stateDoc.clientDoc)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearErrorMessage should set errorMessage to null`() = runTest {
        // Configuramos para que falle y asigne el error
        coEvery { pdfService.generateAndSaveTicket(any(), any(), any(), any()) } returns Result.failure(
            Exception(
                "My error"
            )
        )

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            // Disparamos la descarga para forzar el estado de error
            viewModel.downloadTicket()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            val errorState = expectMostRecentItem()
            assertEquals("Error PDF: My error", errorState.errorMessage)

            // Ahora disparamos el clear
            viewModel.clearErrorMessage()
            val state = awaitItem()
            assertNull(state.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onScanClick should update showSelectionSheet to true`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.onScanClick()
            val state = awaitItem()
            assertTrue(state.showSelectionSheet)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initiatePrintTicket should update printingStatus`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.initiatePrintTicket()
            val state = awaitItem()
            assertEquals("Iniciando impresión...", state.printingStatus)

            viewModel.clearPrintingStatus()
            val stateClear = awaitItem()
            assertNull(stateClear.printingStatus)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `finalizeSale should call completeSaleUseCase and clear temporaries`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val stateWithProducts = awaitItem()
            assertTrue(stateWithProducts.products.isNotEmpty())

            viewModel.finalizeSale()

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val finalState = expectMostRecentItem()
            assertFalse(finalState.isLoading)
            assertEquals("Venta finalizada y stock actualizado", finalState.downloadStatus)

            coVerify { completeSaleUseCase(any(), any()) }
            coVerify { temporaryProductUseCases.clearAll() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `finalizeSale should catch error and show errorMessage`() = runTest {
        coEvery { completeSaleUseCase(any(), any()) } throws Exception("Failed to save Ticket")

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.finalizeSale()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val finalState = expectMostRecentItem()
            assertFalse(finalState.isLoading)
            assertEquals("Error al guardar: Failed to save Ticket", finalState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `downloadTicket should handle success from pdfService`() = runTest {
        coEvery { pdfService.generateAndSaveTicket(any(), any(), any(), any()) } returns Result.success(Unit)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.downloadTicket()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val finalState = expectMostRecentItem()
            assertFalse(finalState.isLoading)
            assertEquals("PDF guardado exitosamente", finalState.downloadStatus)

            // Y de paso testeamos la limpieza de ese estado
            viewModel.clearDownloadStatus()
            val clearState = awaitItem()
            assertNull(clearState.downloadStatus)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `downloadTicket should handle failure from pdfService`() = runTest {
        coEvery { pdfService.generateAndSaveTicket(any(), any(), any(), any()) } returns Result.failure(
            Exception(
                "PDF Error format"
            )
        )

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases, ensureCurrentUserUseCase
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.downloadTicket()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val finalState = expectMostRecentItem()
            assertFalse(finalState.isLoading)
            assertEquals("Error PDF: PDF Error format", finalState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }
}