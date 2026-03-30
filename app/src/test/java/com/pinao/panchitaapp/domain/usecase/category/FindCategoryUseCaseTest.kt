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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FindCategoryUseCaseTest {

    private lateinit var useCase: FindCategoryUseCase
    private val repository: CategoryRepository = mockk()

    @Before
    fun setup() {
        useCase = FindCategoryUseCase(repository)
    }

    @Test
    fun `invoke should return category flow from repository`() = runTest {
        val categoryCode = "cat123"
        val expectedCategory = CategoryModel(categoryId = categoryCode, name = "Lácteos")
        
        every { repository.findCodeCategory(categoryCode) } returns flowOf(expectedCategory)

        val resultFlow = useCase(categoryCode)

        resultFlow.test {
            val item = awaitItem()
            assertEquals("cat123", item?.categoryId)
            assertEquals("Lácteos", item?.name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.findCodeCategory(categoryCode) }
    }

    @Test
    fun `invoke should return null flow from repository when not found`() = runTest {
        val categoryCode = "not_found"
        every { repository.findCodeCategory(categoryCode) } returns flowOf(null)

        val resultFlow = useCase(categoryCode)

        resultFlow.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.findCodeCategory(categoryCode) }
    }
}