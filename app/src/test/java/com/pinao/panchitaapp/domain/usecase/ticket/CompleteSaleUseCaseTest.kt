package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.repository.SaleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CompleteSaleUseCaseTest {

    private lateinit var useCase: CompleteSaleUseCase
    private val repository: SaleRepository = mockk()

    @Before
    fun setup() {
        useCase = CompleteSaleUseCase(repository)
    }

    @Test
    fun `invoke should call saveFullSale on repository with ticket and products`() = runTest {
        val ticket = SaleModel(saleId = "t1", totalAmount = 100.0)
        val products = listOf(
            ProductModel(productId = "p1", name = "Producto A", stockQuantity = 2.0)
        )
        
        coEvery { repository.saveFullSale(any(), any()) } returns Unit

        useCase(ticket, products)

        coVerify(exactly = 1) { 
            repository.saveFullSale(
                withArg { assertEquals("t1", it.saleId) },
                withArg { 
                    assertEquals(1, it.size)
                    assertEquals("p1", it[0].productId)
                }
            ) 
        }
    }
}