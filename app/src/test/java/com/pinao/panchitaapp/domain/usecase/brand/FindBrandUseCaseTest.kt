package com.pinao.panchitaapp.domain.usecase.brand

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.repository.BrandRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FindBrandUseCaseTest {

    private lateinit var useCase: FindBrandUseCase
    private val repository: BrandRepository = mockk()

    @Before
    fun setup() {
        useCase = FindBrandUseCase(repository)
    }

    @Test
    fun `invoke should return mapped flow of BrandModel from repository when id exists`() = runTest {
        val testId = 1
        val expectedBrand = BrandModel(brandId = "b1", name = "Coca-Cola")
        
        every { repository.getBrandById(testId) } returns flowOf(expectedBrand)

        val resultFlow = useCase(testId)

        resultFlow.test {
            val item = awaitItem()
            assertEquals("b1", item?.brandId)
            assertEquals("Coca-Cola", item?.name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.getBrandById(testId) }
    }

    @Test
    fun `invoke should return null flow when id does not exist`() = runTest {
        val testId = 999
        every { repository.getBrandById(testId) } returns flowOf(null)

        val resultFlow = useCase(testId)

        resultFlow.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.getBrandById(testId) }
    }
}