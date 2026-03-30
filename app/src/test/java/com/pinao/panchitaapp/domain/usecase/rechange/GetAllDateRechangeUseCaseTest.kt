package com.pinao.panchitaapp.domain.usecase.rechange

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.RechangeModel
import com.pinao.panchitaapp.domain.repository.RechangeRepository
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
class GetAllDateRechangeUseCaseTest {

    private lateinit var useCase: GetAllDateRechangeUseCase
    private val repository: RechangeRepository = mockk()

    @Before
    fun setup() {
        useCase = GetAllDateRechangeUseCase(repository)
    }

    @Test
    fun `invoke should return mapped flow of all rechanges from database`() = runTest {
        val expectedRechanges = listOf(
            RechangeModel(id = "r1", date = "2023-11-01", amount = 100, numPhone = "123456789"),
            RechangeModel(id = "r2", date = "2023-11-02", amount = 200, numPhone = "987654321")
        )
        
        every { repository.listAllDateRechangeFromDataBase() } returns flowOf(expectedRechanges)

        val resultFlow = useCase()

        resultFlow.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("r1", items[0].id)
            assertEquals("r2", items[1].id)
            assertEquals(200, items[1].amount)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.listAllDateRechangeFromDataBase() }
    }
}