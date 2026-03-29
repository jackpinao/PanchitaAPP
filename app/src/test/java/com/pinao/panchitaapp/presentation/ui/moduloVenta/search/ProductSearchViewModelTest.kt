package com.pinao.panchitaapp.presentation.ui.moduloVenta.search

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.test.utils.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductSearchViewModelTest {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: ProductSearchViewModel
    private val productUseCases: ProductUseCases = mockk()

    private val sampleProducts = listOf(
        ProductModel(productId = "1", name = "Producto A", barcode = "111"),
        ProductModel(productId = "2", name = "Producto B", barcode = "222")
    )

    @Before
    fun setup() {
        // Por defecto para la inicialización (el query inicial es vacío)
        every { productUseCases.search("") } returns flowOf(emptyList())
    }

    @Test
    fun `onQueryChange should update searchQuery state flow`() = runTest {
        viewModel = ProductSearchViewModel(productUseCases)

        viewModel.searchQuery.test {
            assertEquals("", awaitItem()) // Estado inicial

            viewModel.onQueryChange("Café")
            assertEquals("Café", awaitItem())
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `products flow should emit result after debounce time when query changes`() = runTest {
        val query = "Producto A"
        every { productUseCases.search(query) } returns flowOf(listOf(sampleProducts[0]))
        
        viewModel = ProductSearchViewModel(productUseCases)

        // Estabilizamos la búsqueda inicial (el query "") y su debounce de 300ms
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.products.test {
            // Recogemos el valor actual del StateFlow (que es emptyList)
            assertTrue(awaitItem().isEmpty())
            
            // Act: Cambiar la query
            viewModel.onQueryChange(query)

            // Assert: Si avanzamos el tiempo menos que el debounce (ej. 200ms), NO deberíamos tener resultados
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(200)
            expectNoEvents()

            // Ahora avanzamos el tiempo restante para superar el debounce (300ms)
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(101)

            // Esperamos que el flow emita la nueva lista filtrada
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Producto A", result[0].name)
            
            verify(exactly = 1) { productUseCases.search(query) }
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}