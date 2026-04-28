package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshProductsUseCaseTest {

    private lateinit var useCase: RefreshProductsUseCase
    private val productRepository: ProductRepository = mockk()

    @Before
    fun setup() {
        useCase = RefreshProductsUseCase(productRepository)
    }

    @Test
    fun `invoke should call refreshProductsFromRemote`() = runTest {
        // Arrange
        coEvery { productRepository.refreshProductsFromRemote() } returns Unit

        // Act
        useCase()

        // Assert
        coVerify(exactly = 1) { productRepository.refreshProductsFromRemote() }
    }
}