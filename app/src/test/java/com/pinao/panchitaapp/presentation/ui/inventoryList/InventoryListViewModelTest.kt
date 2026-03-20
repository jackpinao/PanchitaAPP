package com.pinao.panchitaapp.presentation.ui.inventoryList

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryListViewModelTest {

    private lateinit var viewModel: InventoryListViewModel
    private val productUseCases: ProductUseCases = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock inicial para el init del ViewModel
        every { productUseCases.getAll() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should observe products and update state to Success`() = runTest {
        // Given
        val products = listOf(
            ProductModel(productId = "1", name = "Product 1"),
            ProductModel(productId = "2", name = "Product 2")
        )
        every { productUseCases.getAll() } returns flowOf(products)

        // When
        viewModel = InventoryListViewModel(productUseCases)
        advanceUntilIdle()

        // Then
        val currentState = viewModel.uiState.value
        assert(currentState is InventoryListUiState.Success)
        assert(currentState.inventoryList.size == 2)
        assert(currentState.inventoryList[0].name == "Product 1")
    }

    @Test
    fun `onSearchQueryChange should update search query and trigger search use case`() = runTest {
        // Given
        viewModel = InventoryListViewModel(productUseCases)
        val query = "Inka"
        val filteredProducts = listOf(ProductModel(productId = "1", name = "Inka Kola"))
        every { productUseCases.search(query) } returns flowOf(filteredProducts)

        // When
        viewModel.onSearchQueryChange(query)
        advanceUntilIdle() // Necesario por el debounce(300L)

        // Then
        assert(viewModel.searchQuery.value == query)
        val currentState = viewModel.uiState.value
        assert(currentState is InventoryListUiState.Success)
        assert(currentState.inventoryList.any { it.name == "Inka Kola" })
        coVerify { productUseCases.search(query) }
    }

    @Test
    fun `onDeleteClick should call delete use case`() = runTest {
        // Given
        val product = ProductModel(productId = "1", name = "To Delete")
        every { productUseCases.getAll() } returns flowOf(listOf(product))
        viewModel = InventoryListViewModel(productUseCases)
        advanceUntilIdle()

        // When
        viewModel.onDeleteClick("1")
        advanceUntilIdle()

        // Then
        coVerify { productUseCases.delete(product) }
    }
}
