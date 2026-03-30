package com.pinao.panchitaapp.domain.usecase.client

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.repository.ClientRepository
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
class GetAllClientsUseCaseTest {

    private lateinit var useCase: GetAllClientsUseCase
    private val repository: ClientRepository = mockk()

    @Before
    fun setup() {
        useCase = GetAllClientsUseCase(repository)
    }

    @Test
    fun `invoke should return mapped flow of clients from repository`() = runTest {
        val expectedClients = listOf(
            ClientModel(id = "c1", name = "Juan Perez", numDoc = "111", active = true, dateCreate = "2023-01-01"),
            ClientModel(id = "c2", name = "Maria Lopez", numDoc = "222", active = false, dateCreate = "2023-02-01")
        )
        every { repository.getClients() } returns flowOf(expectedClients)

        val resultFlow = useCase()

        resultFlow.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("c1", items[0].id)
            assertEquals("Maria Lopez", items[1].name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.getClients() }
    }
}