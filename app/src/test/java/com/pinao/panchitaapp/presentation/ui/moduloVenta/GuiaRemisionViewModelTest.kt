package com.pinao.panchitaapp.presentation.ui.moduloVenta

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import com.pinao.panchitaapp.domain.service.TicketPdfService
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
        coEvery { productUseCases.refreshProducts() } returns Unit
        every { temporaryProductUseCases.getAll() } returns flowOf(mockTemporaryProducts)
    }

    @Test
    fun `init should load temporary products into state`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases
        )

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(1, loadedState.products.size)
            assertEquals("Coca-Cola", loadedState.products[0].name)

            coVerify { productUseCases.refreshProducts() }
        }
    }

    @Test
    fun `startScanningProduct with valid code should update state to showAddDialog`() = runTest {
        coEvery { scanBarcodeUseCase() } returns "123"
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases
        )

        viewModel.uiState.test {
            awaitItem() // Loading
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // Success List

            viewModel.startScanningProduct()

            val scanningState = awaitItem()
            assertFalse(scanningState.showSelectionSheet)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val resultState = awaitItem()
            assertTrue(resultState.showAddDialog)
            assertNotNull(resultState.scannedProduct)
            assertEquals("Coca-Cola", resultState.scannedProduct?.name)
            assertFalse(resultState.isEditing)
            assertEquals("123", resultState.lastScannedCode)
        }
    }

    @Test
    fun `startScanningProduct with invalid code should showNotFoundError`() = runTest {
        coEvery { scanBarcodeUseCase() } returns "999"
        every { productUseCases.findByCode("999") } returns flowOf(null)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases
        )

        viewModel.uiState.test {
            awaitItem() // Loading
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // List loaded

            viewModel.startScanningProduct()

            // Ignoramos el estado intermedio de hide SelectionSheet
            skipItems(1)
            
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState.showNotFoundError)
        }
    }

    @Test
    fun `onQuantityChange should update quantity only with valid numbers`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases
        )

        viewModel.uiState.test {
            awaitItem() // Loading
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // List loaded

            viewModel.onQuantityChange("5")
            val state1 = awaitItem()
            assertEquals("5", state1.quantity)

            viewModel.onQuantityChange("abc")
            expectNoEvents() // No emite nada por fallar el Regex
        }
    }

    @Test
    fun `onConfirmQuantity should add product to temporary list if not editing`() = runTest {
        every { productUseCases.findByCode("123") } returns flowOf(catalogProduct)

        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases
        )

        viewModel.uiState.test {
            awaitItem() // Loading
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // Loaded

            // Simular escaneo
            viewModel.handleProductByCode("123")
            val dialogState = awaitItem()
            assertTrue(dialogState.showAddDialog)
            
            // Setear cantidad a agregar
            viewModel.onQuantityChange("10")
            awaitItem()

            // Confirmar
            viewModel.onConfirmQuantity()
            
            val loadingSaveState = awaitItem()
            assertTrue(loadingSaveState.isLoading)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            
            val finalState = awaitItem()
            assertFalse(finalState.showAddDialog) // Dialog cerrado
            assertFalse(finalState.isLoading)

            // Verifica que el catálogo se actualiza restando el stock
            coVerify { productUseCases.save(match { it.stockQuantity == 40.0 }) }
            // Verifica que se guarde en temporales
            coVerify { temporaryProductUseCases.save(match { it.quantity == 10.0 }) }
        }
    }

    @Test
    fun `finalizeSale should call completeSaleUseCase and clear temporaries`() = runTest {
        viewModel = GuiaRemisionViewModel(
            productUseCases, saveClientUseCase, scanBarcodeUseCase,
            completeSaleUseCase, pdfService, temporaryProductUseCases
        )

        viewModel.uiState.test {
            awaitItem() // Loading
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            val stateWithProducts = awaitItem() // Loaded with mockTemporaryProducts

            // Aseguramos que haya productos cargados en UI
            assertTrue(stateWithProducts.products.isNotEmpty())

            viewModel.finalizeSale()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals("Venta finalizada y stock actualizado", finalState.downloadStatus)

            coVerify { completeSaleUseCase(any(), any()) }
            coVerify { temporaryProductUseCases.clearAll() }
        }
    }
}