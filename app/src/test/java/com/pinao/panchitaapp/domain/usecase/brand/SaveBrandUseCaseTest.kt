package com.pinao.panchitaapp.domain.usecase.brand

import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.repository.BrandRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveBrandUseCaseTest {

    private lateinit var useCase: SaveBrandUseCase
    private val repository: BrandRepository = mockk()

    @Before
    fun setup() {
        useCase = SaveBrandUseCase(repository)
    }

    @Test
    fun `invoke should call saveBrand on repository with correct brand`() = runTest {
        val modelToSave = BrandModel(brandId = "b1", name = "Inca Kola")
        coEvery { repository.saveBrand(any()) } returns Unit

        useCase(modelToSave)

        coVerify(exactly = 1) {
            repository.saveBrand(withArg { brand ->
                assertEquals("b1", brand.brandId)
                assertEquals("Inca Kola", brand.name)
            })
        }
    }
}