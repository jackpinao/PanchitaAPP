package com.pinao.panchitaapp.domain.usecase.products

import android.util.Log
import com.pinao.panchitaapp.domain.repository.AuthRepository
import com.pinao.panchitaapp.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshProductsUseCaseTest {

    private lateinit var useCase: RefreshProductsUseCase
    private val productRepository: ProductRepository = mockk()
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        useCase = RefreshProductsUseCase(productRepository, authRepository)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `invoke should call refreshProductsFromRemote when user is logged in`() = runTest {
        // Arrange
        every { authRepository.isUserLoggedIn() } returns true
        coEvery { productRepository.refreshProductsFromRemote() } returns Unit

        // Act
        useCase()

        // Assert
        verify(exactly = 1) { authRepository.isUserLoggedIn() }
        coVerify(exactly = 1) { productRepository.refreshProductsFromRemote() }
    }

    @Test
    fun `invoke should skip refreshProductsFromRemote when user is not logged in`() = runTest {
        // Arrange
        every { authRepository.isUserLoggedIn() } returns false
        coEvery { productRepository.refreshProductsFromRemote() } returns Unit

        // Act
        useCase()

        // Assert
        verify(exactly = 1) { authRepository.isUserLoggedIn() }
        // Verificamos que no se intentó refrescar nada
        coVerify(exactly = 0) { productRepository.refreshProductsFromRemote() }
    }
}