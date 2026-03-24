package com.pinao.panchitaapp.presentation.ui.inventoryList

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [InventoryListViewModel] using Turbine for Flow testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InventoryListViewModelTest {

    private lateinit var viewModel: InventoryListViewModel
    private val productUseCases: ProductUseCases = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val sampleProducts = listOf(
        ProductModel(productId = "1", name = "Arroz", barcode = "775123"),
        ProductModel(productId = "2", name = "Azúcar", barcode = "775456")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Default mock for init {} block
        every { productUseCases.getAll() } returns flowOf(sampleProducts)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load all products and update state to Success`() = runTest {
        viewModel = InventoryListViewModel(productUseCases)

        viewModel.uiState.test {
            // Inicia con Loading según el StateFlow inicial
            assertTrue(awaitItem() is InventoryListUiState.Loading)

            // Avanzamos el debounce del init
            testScheduler.advanceTimeBy(301)

            // Pasa a Success con los datos cargados
            val state = awaitItem()
            assertTrue("Debe estar en Success", state is InventoryListUiState.Success)
            assertEquals(2, state.inventoryList.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChange should trigger search after debounce time`() = runTest {
        val query = "Arroz"
        every { productUseCases.search(query) } returns flowOf(listOf(sampleProducts[0]))
        viewModel = InventoryListViewModel(productUseCases)

        viewModel.uiState.test {
            awaitItem() // Loading inicial
            testScheduler.advanceTimeBy(301)
            awaitItem() // Success inicial

            viewModel.onSearchQueryChange(query)

            // No hay cambio antes de los 300ms
            testScheduler.advanceTimeBy(200)

            // Pasamos el umbral del debounce
            testScheduler.advanceTimeBy(101)

            val state = awaitItem()
            assertTrue(state is InventoryListUiState.Success)
            assertEquals(1, state.inventoryList.size)
            assertEquals("Arroz", state.inventoryList[0].name)
            coVerify { productUseCases.search(query) }
        }
    }

    @Test
    fun `onRefresh should call refreshProducts and show Loading state`() = runTest {
        viewModel = InventoryListViewModel(productUseCases)

        viewModel.uiState.test {
            awaitItem() // Loading inicial
            testScheduler.advanceTimeBy(301)
            awaitItem() // Success inicial

            viewModel.onRefresh()

            // Capturamos el Loading emitido por el refresh
            val state = awaitItem()
            assertTrue(
                "Debe cambiar a Loading durante el refresh",
                state is InventoryListUiState.Loading
            )

            coVerify { productUseCases.refreshProducts() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onItemClick should emit NavigateToEdit event with correct barcode`() = runTest {
        viewModel = InventoryListViewModel(productUseCases)
        testScheduler.advanceTimeBy(301) // Dejamos que cargue la lista

        viewModel.eventFlow.test {
            viewModel.onItemClick("1")

            val event = awaitItem()
            assertTrue(event is InventoryListViewModel.InventoryListEvent.NavigateToEdit)
            assertEquals(
                "775123",
                (event as InventoryListViewModel.InventoryListEvent.NavigateToEdit).barcode
            )
        }
    }

    @Test
    fun `onDeleteClick should invoke delete use case with correct product`() = runTest {
        viewModel = InventoryListViewModel(productUseCases)
        testScheduler.advanceTimeBy(301)

        viewModel.onDeleteClick("2")
        testScheduler.runCurrent()

        coVerify { productUseCases.delete(match { it.productId == "2" }) }
    }
}
