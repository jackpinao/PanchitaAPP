package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveProductsUseCaseTest {

    private lateinit var useCase: SaveProductsUseCase
    private val repository: ProductRepository = mockk()

    @Before
    fun setup() {
        useCase = SaveProductsUseCase(repository)
    }

    @Test
    fun `invoke should call saveProduct on repository with correct product`() = runTest {
        val modelToSave = ProductModel(productId = "prod1", name = "Test Product", priceSell = 15.0)
        coEvery { repository.saveProduct(any()) } returns Unit

        useCase(modelToSave)

        coVerify(exactly = 1) {
            repository.saveProduct(withArg { product ->
                assertEquals("prod1", product.productId)
                assertEquals("Test Product", product.name)
                assertEquals(15.0, product.priceSell, 0.0)
            })
        }
    }
}