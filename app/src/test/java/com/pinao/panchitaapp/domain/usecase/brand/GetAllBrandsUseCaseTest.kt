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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllBrandsUseCaseTest {

    private lateinit var useCase: GetAllBrandsUseCase
    private val repository: BrandRepository = mockk()

    @Before
    fun setup() {
        useCase = GetAllBrandsUseCase(repository)
    }

    @Test
    fun `invoke should return mapped flow of brands from database`() = runTest {
        val brands = listOf(
            BrandModel(brandId = "b1", name = "Coca-Cola"),
            BrandModel(brandId = "b2", name = "Inca Kola")
        )
        every { repository.getAllBrands() } returns flowOf(brands)

        val resultFlow = useCase()

        resultFlow.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("b1", items[0].brandId)
            assertEquals("Inca Kola", items[1].name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.getAllBrands() }
    }
}