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
class GetListForDateRechangeUCTest {

    private lateinit var useCase: GetListForDateRechangeUC
    private val repository: RechangeRepository = mockk()

    @Before
    fun setup() {
        useCase = GetListForDateRechangeUC(repository)
    }

    @Test
    fun `invoke should return mapped flow of rechanges for a specific date`() = runTest {
        val testDate = "2023-11-01"
        val expectedRechanges = listOf(
            RechangeModel(id = "r1", date = testDate, amount = 100, numPhone = "123456789")
        )
        
        every { repository.listForDate(testDate) } returns flowOf(expectedRechanges)

        val resultFlow = useCase(testDate)

        resultFlow.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("r1", items[0].id)
            assertEquals(testDate, items[0].date)
            assertEquals(100, items[0].amount)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.listForDate(testDate) }
    }
}