package com.pinao.panchitaapp.domain.usecase.category

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllCategoriesUseCaseTest {

    private lateinit var useCase: GetAllCategoriesUseCase
    private val repository: CategoryRepository = mockk()

    @Before
    fun setup() {
        useCase = GetAllCategoriesUseCase(repository)
    }

    @Test
    fun `invoke should return flow from repository`() = runTest {
        val categories = listOf(
            CategoryModel(categoryId = "1", name = "Bebidas"),
            CategoryModel(categoryId = "2", name = "Snacks")
        )
        every { repository.getAllCategoriesFromDataBase() } returns flowOf(categories)

        val resultFlow = useCase()

        resultFlow.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("Bebidas", items[0].name)
            assertEquals("Snacks", items[1].name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.getAllCategoriesFromDataBase() }
    }
}