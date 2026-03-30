package com.pinao.panchitaapp.domain.usecase.user

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.UserRepository
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
class ListUserUseCaseTest {

    private lateinit var useCase: ListUserUseCase
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        useCase = ListUserUseCase(repository)
    }

    @Test
    fun `invoke should return Flow of List UserModel from repository`() = runTest {
        val query = "Juan"
        val expectedUsers = listOf(
            UserModel(userId = "1", name = "Juan Perez")
        )
        every { repository.listDate(query) } returns flowOf(expectedUsers)

        val resultFlow = useCase(query)

        resultFlow.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Juan Perez", items[0].name)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.listDate(query) }
    }
}