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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FindCodeProductUseCaseTest {

    private lateinit var useCase: FindCodeProductUseCase
    private val repository: ProductRepository = mockk()

    @Before
    fun setup() {
        useCase = FindCodeProductUseCase(repository)
    }

    @Test
    fun `invoke should return mapped product from repository when code exists`() = runTest {
        val code = "775123"
        val expectedProduct = ProductModel(productId = "p1", name = "Agua", barcode = code)
        every { repository.findCodeProduct(code) } returns flowOf(expectedProduct)

        val resultFlow = useCase(code)

        resultFlow.test {
            val item = awaitItem()
            assertEquals("p1", item?.productId)
            assertEquals("Agua", item?.name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.findCodeProduct(code) }
    }

    @Test
    fun `invoke should return null flow when code does not exist`() = runTest {
        val code = "000000"
        every { repository.findCodeProduct(code) } returns flowOf(null)

        val resultFlow = useCase(code)

        resultFlow.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.findCodeProduct(code) }
    }
}