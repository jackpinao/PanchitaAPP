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
class DeleteClientUseCaseTest {

    private lateinit var useCase: DeleteClientUseCase
    private val repository: ClientRepository = mockk()

    @Before
    fun setup() {
        useCase = DeleteClientUseCase(repository)
    }

    @Test
    fun `invoke should call deleteClient on repository with correct model`() = runTest {
        val modelToDelete = ClientModel(id = "c1", name = "Test Client", numDoc = "12345678", active = true, dateCreate = "2023-10-25")
        coEvery { repository.deleteClient(any()) } returns Unit

        useCase(modelToDelete)

        coVerify(exactly = 1) {
            repository.deleteClient(withArg { client ->
                assertEquals("c1", client.id)
                assertEquals("Test Client", client.name)
            })
        }
    }
}