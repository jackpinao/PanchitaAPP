package com.pinao.panchitaapp.data.repository

import app.cash.turbine.test
import com.pinao.panchitaapp.data.local.dao.ClientDao
import com.pinao.panchitaapp.data.local.entity.ClientEntity
import com.pinao.panchitaapp.domain.model.ClientModel
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
class ClientRepositoryImplTest {

    private lateinit var repository: ClientRepositoryImpl
    private val mockDao: ClientDao = mockk()

    @Before
    fun setup() {
        repository = ClientRepositoryImpl(mockDao)
    }

    @Test
    fun `getClients should emit mapped list of ClientModel`() = runTest {
        val entities = listOf(
            ClientEntity("c1", "Juan Perez", "11111111", true, "2023-01-01"),
            ClientEntity("c2", "Maria Lopez", "22222222", false, "2023-02-01")
        )
        every { mockDao.getClients() } returns flowOf(entities)

        repository.getClients().test {
            val items = awaitItem()
            assertEquals(2, items.size)
            
            assertEquals("c1", items[0].id)
            assertEquals("Juan Perez", items[0].name)
            assertEquals(true, items[0].active)
            
            assertEquals("c2", items[1].id)
            assertEquals("Maria Lopez", items[1].name)
            assertEquals(false, items[1].active)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        coVerify(exactly = 1) { mockDao.getClients() }
    }

    @Test
    fun `addClient should call insert on Dao with mapped entity`() = runTest {
        val model = ClientModel("c3", "Pedro", "33333333", true, "2023-03-01")
        coEvery { mockDao.insert(any()) } returns Unit

        repository.addClient(model)

        coVerify(exactly = 1) {
            mockDao.insert(withArg { entity ->
                assertEquals("c3", entity.clientId)
                assertEquals("Pedro", entity.name)
            })
        }
    }

    @Test
    fun `updateClient should call update on Dao with mapped entity`() = runTest {
        val model = ClientModel("c4", "Luis", "44444444", true, "2023-04-01")
        coEvery { mockDao.update(any()) } returns Unit

        repository.updateClient(model)

        coVerify(exactly = 1) {
            mockDao.update(withArg { entity ->
                assertEquals("c4", entity.clientId)
                assertEquals("Luis", entity.name)
            })
        }
    }

    @Test
    fun `deleteClient should call delete on Dao with mapped entity`() = runTest {
        val model = ClientModel("c5", "Ana", "55555555", false, "2023-05-01")
        coEvery { mockDao.delete(any()) } returns Unit

        repository.deleteClient(model)

        coVerify(exactly = 1) {
            mockDao.delete(withArg { entity ->
                assertEquals("c5", entity.clientId)
                assertEquals("Ana", entity.name)
            })
        }
    }
}