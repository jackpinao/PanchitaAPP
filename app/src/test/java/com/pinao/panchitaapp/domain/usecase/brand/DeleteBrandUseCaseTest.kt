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
class DeleteBrandUseCaseTest {

    private lateinit var useCase: DeleteBrandUseCase
    private val repository: BrandRepository = mockk()

    @Before
    fun setup() {
        useCase = DeleteBrandUseCase(repository)
    }

    @Test
    fun `invoke should call deleteBrand on repository with correct brand`() = runTest {
        val modelToDelete = BrandModel(brandId = "b1", name = "Delete Me")
        coEvery { repository.deleteBrand(any()) } returns Unit

        useCase(modelToDelete)

        coVerify(exactly = 1) {
            repository.deleteBrand(withArg { brand ->
                assertEquals("b1", brand.brandId)
                assertEquals("Delete Me", brand.name)
            })
        }
    }
}