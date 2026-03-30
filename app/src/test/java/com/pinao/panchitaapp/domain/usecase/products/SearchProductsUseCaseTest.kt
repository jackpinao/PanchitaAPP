package com.pinao.panchitaapp.domain.usecase.products

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchProductsUseCaseTest {

    private lateinit var useCase: SearchProductsUseCase
    private val repository: ProductRepository = mockk()

    @Before
    fun setup() {
        useCase = SearchProductsUseCase(repository)
    }

    @Test
    fun `invoke should return filtered products flow based on query`() = runTest {
        val query = "Inca"
        val expectedProducts = listOf(
            ProductModel(productId = "p1", name = "Inca Kola 500ml")
        )
        every { repository.searchProducts(query) } returns flowOf(expectedProducts)

        val resultFlow = useCase(query)

        resultFlow.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Inca Kola 500ml", items[0].name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.searchProducts(query) }
    }
}