package com.pinao.panchitaapp.domain.usecase.temporary

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import com.pinao.panchitaapp.domain.repository.TemporaryProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class TemporaryProductUseCasesTest {

    private val repository: TemporaryProductRepository = mockk()

    // Instancias de cada caso de uso individual contenido en el archivo
    private lateinit var getAllUseCase: GetAllTemporaryProductsUseCase
    private lateinit var saveUseCase: SaveTemporaryProductUseCase
    private lateinit var deleteUseCase: DeleteTemporaryProductUseCase
    private lateinit var clearAllUseCase: ClearTemporaryProductsUseCase

    @Before
    fun setup() {
        getAllUseCase = GetAllTemporaryProductsUseCase(repository)
        saveUseCase = SaveTemporaryProductUseCase(repository)
        deleteUseCase = DeleteTemporaryProductUseCase(repository)
        clearAllUseCase = ClearTemporaryProductsUseCase(repository)
    }

    @Test
    fun `GetAllTemporaryProductsUseCase should return flow from repository`() = runTest {
        val expectedList = listOf(
            TemporaryProductModel(id = "1", productId = "p1", name = "Prod 1", code = "111", price = 10.0, quantity = 2.0, priceExcludingIGV = 8.5)
        )
        every { repository.getTemporaryProducts() } returns flowOf(expectedList)

        val resultFlow = getAllUseCase()

        resultFlow.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("p1", items[0].productId)
            
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 1) { repository.getTemporaryProducts() }
    }

    @Test
    fun `SaveTemporaryProductUseCase should call saveTemporaryProduct on repository`() = runTest {
        val item = TemporaryProductModel(id = "2", productId = "p2", name = "Prod 2", code = "222", price = 15.0, quantity = 1.0, priceExcludingIGV = 12.5)
        coEvery { repository.saveTemporaryProduct(any()) } returns Unit

        saveUseCase(item)

        coVerify(exactly = 1) { 
            repository.saveTemporaryProduct(withArg { 
                assertEquals("2", it.id)
                assertEquals("p2", it.productId)
            }) 
        }
    }

    @Test
    fun `DeleteTemporaryProductUseCase should call deleteTemporaryProduct on repository`() = runTest {
        val item = TemporaryProductModel(id = "3", productId = "p3", name = "Prod 3", code = "333", price = 20.0, quantity = 3.0, priceExcludingIGV = 16.0)
        coEvery { repository.deleteTemporaryProduct(any()) } returns Unit

        deleteUseCase(item)

        coVerify(exactly = 1) { 
            repository.deleteTemporaryProduct(withArg { 
                assertEquals("3", it.id)
                assertEquals("p3", it.productId)
            }) 
        }
    }

    @Test
    fun `ClearTemporaryProductsUseCase should call clearAllTemporaryProducts on repository`() = runTest {
        coEvery { repository.clearAllTemporaryProducts() } returns Unit

        clearAllUseCase()

        coVerify(exactly = 1) { repository.clearAllTemporaryProducts() }
    }

    @Test
    fun `TemporaryProductUseCases equals returns true for same property instances`() {
        val bundle1 = TemporaryProductUseCases(getAllUseCase, saveUseCase, deleteUseCase, clearAllUseCase)
        val bundle2 = TemporaryProductUseCases(getAllUseCase, saveUseCase, deleteUseCase, clearAllUseCase)

        assertEquals(bundle1, bundle2)
    }

    @Test
    fun `TemporaryProductUseCases equals returns false for different property instances`() {
        val bundle1 = TemporaryProductUseCases(getAllUseCase, saveUseCase, deleteUseCase, clearAllUseCase)
        val bundle2 = TemporaryProductUseCases(GetAllTemporaryProductsUseCase(mockk()), saveUseCase, deleteUseCase, clearAllUseCase)

        assert(bundle1 != bundle2)
    }

    @Test
    fun `TemporaryProductUseCases equals returns false when compared to null`() {
        val bundle = TemporaryProductUseCases(getAllUseCase, saveUseCase, deleteUseCase, clearAllUseCase)

        assert(bundle != null)
    }
}