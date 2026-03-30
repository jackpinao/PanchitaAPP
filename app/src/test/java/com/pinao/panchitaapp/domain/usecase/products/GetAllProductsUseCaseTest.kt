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
class GetAllProductsUseCaseTest {

    private lateinit var useCase: GetAllProductsUseCase
    private val repository: ProductRepository = mockk()

    @Before
    fun setup() {
        useCase = GetAllProductsUseCase(repository)
    }

    @Test
    fun `invoke should return mapped flow of products from database`() = runTest {
        val products = listOf(
            ProductModel(productId = "p1", name = "Prod1"),
            ProductModel(productId = "p2", name = "Prod2")
        )
        every { repository.getAllProductsFromDataBase() } returns flowOf(products)

        val resultFlow = useCase()

        resultFlow.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("p1", items[0].productId)
            assertEquals("Prod2", items[1].name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.getAllProductsFromDataBase() }
    }
}