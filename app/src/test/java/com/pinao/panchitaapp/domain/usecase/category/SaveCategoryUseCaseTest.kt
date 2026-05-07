package com.pinao.panchitaapp.domain.usecase.category

import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveCategoryUseCaseTest {

    private lateinit var useCase: SaveCategoryUseCase
    private val repository: CategoryRepository = mockk()

    @Before
    fun setup() {
        useCase = SaveCategoryUseCase(repository)
    }

    @Test
    fun `invoke should call saveCategory on repository with given model`() = runTest {
        // Given
        val categoryToSave = CategoryModel(categoryId = "cat1", name = "Verduras")
        coEvery { repository.saveCategory(any()) } returns Unit

        // When
        useCase(categoryToSave)

        // Then
        coVerify(exactly = 1) { 
            repository.saveCategory(withArg { 
                assertEquals("cat1", it.categoryId)
                assertEquals("Verduras", it.name)
            }) 
        }
    }
}