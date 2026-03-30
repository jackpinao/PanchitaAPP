package com.pinao.panchitaapp.domain.usecase.client

import com.pinao.panchitaapp.domain.model.ClientModel
import com.pinao.panchitaapp.domain.repository.ClientRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveClientUseCaseTest {

    private lateinit var useCase: SaveClientUseCase
    private val repository: ClientRepository = mockk()

    @Before
    fun setup() {
        useCase = SaveClientUseCase(repository)
    }

    @Test
    fun `invoke should call addClient on repository with correct model`() = runTest {
        val modelToSave = ClientModel(id = "c1", name = "Test Client", numDoc = "12345678", active = true, dateCreate = "2023-10-25")
        coEvery { repository.addClient(any()) } returns Unit

        useCase(modelToSave)

        coVerify(exactly = 1) {
            repository.addClient(withArg { client ->
                assertEquals("c1", client.id)
                assertEquals("Test Client", client.name)
            })
        }
    }
}