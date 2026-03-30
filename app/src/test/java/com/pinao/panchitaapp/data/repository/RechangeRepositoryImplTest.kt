package com.pinao.panchitaapp.data.repository

import app.cash.turbine.test
import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import com.pinao.panchitaapp.data.network.rechange.RechangeService
import com.pinao.panchitaapp.domain.model.RechangeModel
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
class RechangeRepositoryImplTest {

    private lateinit var repository: RechangeRepositoryImpl
    private val mockDao: RechangeDao = mockk()
    private val mockApi: RechangeService = mockk()

    @Before
    fun setup() {
        repository = RechangeRepositoryImpl(api = mockApi, rechangeDao = mockDao)
    }

    @Test
    fun `listAllDateRechangeFromDataBase should emit mapped list of RechangeModel`() = runTest {
        // Given
        val entities = listOf(
            RechangeEntity(id = "1", date = "2023-11-01", amount = 50, numPhone = "999111222"),
            RechangeEntity(id = "2", date = "2023-11-02", amount = 100, numPhone = "999333444")
        )
        every { mockDao.getAllDateRechange() } returns flowOf(entities)

        // When
        val resultFlow = repository.listAllDateRechangeFromDataBase()

        // Then
        resultFlow.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("1", items[0].id)
            assertEquals(50, items[0].amount)
            assertEquals("2", items[1].id)
            assertEquals(100, items[1].amount)
            
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { mockDao.getAllDateRechange() }
    }

    @Test
    fun `listForDate should emit filtered mapped list`() = runTest {
        // Given
        val testDate = "2023-11-01"
        val entities = listOf(
            RechangeEntity(id = "1", date = testDate, amount = 50, numPhone = "999111222")
        )
        every { mockDao.getListForDate(testDate) } returns flowOf(entities)

        // When
        val resultFlow = repository.listForDate(testDate)

        // Then
        resultFlow.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(testDate, items[0].date)
            
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { mockDao.getListForDate(testDate) }
    }

    @Test
    fun `save should call insert on Dao with mapped entity`() = runTest {
        // Given
        val model = RechangeModel(id = "3", date = "2023-11-03", amount = 20, numPhone = "988777666")
        coEvery { mockDao.insert(any()) } returns 1L // Room inserts often return Long

        // When
        repository.save(model)

        // Then
        coVerify(exactly = 1) {
            mockDao.insert(withArg { entity ->
                assertEquals("3", entity.id)
                assertEquals(20, entity.amount)
            })
        }
    }

    @Test
    fun `delete should call delete on Dao with mapped entity`() = runTest {
        // Given
        val model = RechangeModel(id = "4", date = "2023-11-04", amount = 30, numPhone = "999000000")
        coEvery { mockDao.delete(any()) } returns Unit

        // When
        repository.delete(model)

        // Then
        coVerify(exactly = 1) {
            mockDao.delete(withArg { entity ->
                assertEquals("4", entity.id)
                assertEquals(30, entity.amount)
            })
        }
    }

    @Test
    fun `listAllDateRechangeFromApi should return mapped list from API`() = runTest {
        // Given
        val apiResponse = listOf(
            RechangeEntity(id = "5", date = "2023-11-05", amount = 10, numPhone = "123123123")
        )
        coEvery { mockApi.getAllRechanges() } returns apiResponse

        // When
        val result = repository.listAllDateRechangeFromApi()

        // Then
        assertEquals(1, result.size)
        assertEquals("5", result[0].id)
        assertEquals(10, result[0].amount)

        coVerify(exactly = 1) { mockApi.getAllRechanges() }
    }
}