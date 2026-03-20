package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SyncUnsyncedProductsUseCaseTest {

    private lateinit var syncUnsyncedProductsUseCase: SyncUnsyncedProductsUseCase
    private val repository: ProductRepository = mockk()

    @Before
    fun setUp() {
        syncUnsyncedProductsUseCase = SyncUnsyncedProductsUseCase(repository)
    }

    @Test
    fun `when invoke is called then repository syncUnsyncedProducts should be executed`() = runTest {
        // Given
        coEvery { repository.syncUnsyncedProducts() } returns Unit

        // When
        syncUnsyncedProductsUseCase()

        // Then
        coVerify(exactly = 1) { repository.syncUnsyncedProducts() }
    }
}
