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
class SyncUnsyncedProductsUseCaseTest {

    private lateinit var useCase: SyncUnsyncedProductsUseCase
    private val repository: ProductRepository = mockk()

    @Before
    fun setup() {
        useCase = SyncUnsyncedProductsUseCase(repository)
    }

    @Test
    fun `invoke should call syncUnsyncedProducts on repository`() = runTest {
        coEvery { repository.syncUnsyncedProducts() } returns Unit

        useCase()

        coVerify(exactly = 1) { repository.syncUnsyncedProducts() }
    }
}