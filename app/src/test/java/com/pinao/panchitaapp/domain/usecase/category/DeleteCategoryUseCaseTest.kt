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
class DeleteCategoryUseCaseTest {

    private lateinit var useCase: DeleteCategoryUseCase
    private val repository: CategoryRepository = mockk()

    @Before
    fun setup() {
        useCase = DeleteCategoryUseCase(repository)
    }

    @Test
    fun `invoke should call deleteCategory on repository with given model`() = runTest {
        // Given
        val categoryToDelete = CategoryModel(categoryId = "catToDelete", name = "A Borrar")
        coEvery { repository.deleteCategory(any()) } returns Unit

        // When
        useCase(categoryToDelete)

        // Then
        coVerify(exactly = 1) { 
            repository.deleteCategory(withArg { 
                assertEquals("catToDelete", it.categoryId)
                assertEquals("A Borrar", it.name)
            }) 
        }
    }
}