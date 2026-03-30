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
class DeleteProductUseCaseTest {

    private lateinit var useCase: DeleteProductUseCase
    private val repository: ProductRepository = mockk()

    @Before
    fun setup() {
        useCase = DeleteProductUseCase(repository)
    }

    @Test
    fun `invoke should call deleteProduct on repository with correct product`() = runTest {
        val modelToDelete = ProductModel(productId = "prod_del", name = "Delete Me")
        coEvery { repository.deleteProduct(any()) } returns Unit

        useCase(modelToDelete)

        coVerify(exactly = 1) {
            repository.deleteProduct(withArg { product ->
                assertEquals("prod_del", product.productId)
                assertEquals("Delete Me", product.name)
            })
        }
    }
}