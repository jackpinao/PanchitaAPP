package com.pinao.panchitaapp.data.repository

import app.cash.turbine.test
import com.pinao.panchitaapp.data.source.local.dao.TemporaryProductDao
import com.pinao.panchitaapp.data.source.local.entity.TemporaryProductEntity
import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TemporaryProductRepositoryImplTest {

    private lateinit var repository: TemporaryProductRepositoryImpl
    private val mockDao: TemporaryProductDao = mockk()

    @Before
    fun setup() {
        repository = TemporaryProductRepositoryImpl(mockDao)
    }

    @Test
    fun `getTemporaryProducts should return mapped flow of TemporaryProductModel`() = runTest {
        val entities = listOf(
            TemporaryProductEntity(
                id = "t1", productId = "p1", name = "Prod1", code = "111",
                price = 10.0, quantity = 2.0, priceExcludingIGV = 8.5
            )
        )
        every { mockDao.getAllTemporaryProducts() } returns flowOf(entities)

        val result = repository.getTemporaryProducts()

        result.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("t1", items[0].id)
            assertEquals("Prod1", items[0].name)
            
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { mockDao.getAllTemporaryProducts() }
    }

    @Test
    fun `saveTemporaryProduct should call insert on Dao with mapped entity`() = runTest {
        val model = TemporaryProductModel(
            id = "t2", productId = "p2", name = "Prod2", code = "222",
            price = 15.0, quantity = 1.0, priceExcludingIGV = 12.5
        )
        coEvery { mockDao.insert(any()) } returns Unit

        repository.saveTemporaryProduct(model)

        coVerify(exactly = 1) {
            mockDao.insert(withArg { entity ->
                assertEquals("t2", entity.id)
                assertEquals("Prod2", entity.name)
            })
        }
    }

    @Test
    fun `deleteTemporaryProduct should call delete on Dao with mapped entity`() = runTest {
        val model = TemporaryProductModel(
            id = "t3", productId = "p3", name = "Prod3", code = "333",
            price = 20.0, quantity = 3.0, priceExcludingIGV = 16.0
        )
        coEvery { mockDao.delete(any()) } returns Unit

        repository.deleteTemporaryProduct(model)

        coVerify(exactly = 1) {
            mockDao.delete(withArg { entity ->
                assertEquals("t3", entity.id)
                assertEquals("p3", entity.productId)
            })
        }
    }

    @Test
    fun `clearAllTemporaryProducts should call clearAll on Dao`() = runTest {
        coEvery { mockDao.clearAll() } returns Unit

        repository.clearAllTemporaryProducts()

        coVerify(exactly = 1) { mockDao.clearAll() }
    }
}