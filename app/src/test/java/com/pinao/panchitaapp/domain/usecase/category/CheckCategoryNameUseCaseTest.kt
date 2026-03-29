package com.pinao.panchitaapp.domain.usecase.category

import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckCategoryNameUseCaseTest {

    private lateinit var checkCategoryNameUseCase: CheckCategoryNameUseCase
    private val mockRepository: CategoryRepository = mockk()

    @Before
    fun setup() {
        checkCategoryNameUseCase = CheckCategoryNameUseCase(mockRepository)
    }

    @Test
    fun `invoke should return true when category name exists exactly`() = runTest {
        // Given
        val existingCategories = listOf(
            CategoryModel(categoryId = "1", name = "Bebidas", revenue = 10.0),
            CategoryModel(categoryId = "2", name = "Snacks", revenue = 15.0)
        )
        // Simulamos que el repositorio devuelve nuestra lista
        every { mockRepository.getAllCategoriesFromDataBase() } returns flowOf(existingCategories)

        // When
        val result = checkCategoryNameUseCase("Bebidas")

        // Then
        assertTrue("El caso de uso debe retornar true si el nombre exacto existe", result)
        verify(exactly = 1) { mockRepository.getAllCategoriesFromDataBase() }
    }

    @Test
    fun `invoke should return true when category name exists with different case`() = runTest {
        // Given
        val existingCategories = listOf(
            CategoryModel(categoryId = "1", name = "Lácteos", revenue = 5.0)
        )
        every { mockRepository.getAllCategoriesFromDataBase() } returns flowOf(existingCategories)

        // When
        // Verificamos "LÁCTEOS" en mayúsculas, la lógica de ignoreCase = true debe activarse
        val result = checkCategoryNameUseCase("LÁCTEOS")

        // Then
        assertTrue("El caso de uso debe ignorar mayúsculas y minúsculas", result)
    }

    @Test
    fun `invoke should return false when category name does not exist`() = runTest {
        // Given
        val existingCategories = listOf(
            CategoryModel(categoryId = "1", name = "Bebidas", revenue = 10.0),
            CategoryModel(categoryId = "2", name = "Snacks", revenue = 15.0)
        )
        every { mockRepository.getAllCategoriesFromDataBase() } returns flowOf(existingCategories)

        // When
        val result = checkCategoryNameUseCase("Galletas")

        // Then
        assertFalse("El caso de uso debe retornar false si el nombre no existe", result)
    }

    @Test
    fun `invoke should return false when category list is empty`() = runTest {
        // Given
        // El repositorio no tiene categorías guardadas
        every { mockRepository.getAllCategoriesFromDataBase() } returns flowOf(emptyList())

        // When
        val result = checkCategoryNameUseCase("Bebidas")

        // Then
        assertFalse("Debe retornar false si la lista está vacía", result)
    }
}