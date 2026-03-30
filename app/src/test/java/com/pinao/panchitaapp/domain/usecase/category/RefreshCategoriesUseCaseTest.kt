package com.pinao.panchitaapp.domain.usecase.category

import com.pinao.panchitaapp.domain.repository.CategoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshCategoriesUseCaseTest {

    private lateinit var useCase: RefreshCategoriesUseCase
    private val repository: CategoryRepository = mockk()

    @Before
    fun setup() {
        useCase = RefreshCategoriesUseCase(repository)
    }

    @Test
    fun `invoke should call refreshCategoriesFromRemote on repository`() = runTest {
        // Given
        coEvery { repository.refreshCategoriesFromRemote() } returns Unit

        // When
        useCase()

        // Then
        coVerify(exactly = 1) { repository.refreshCategoriesFromRemote() }
    }
}