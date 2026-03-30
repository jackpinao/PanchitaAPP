package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveUserUseCaseTest {

    private lateinit var useCase: SaveUserUseCase
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        useCase = SaveUserUseCase(repository)
    }

    @Test
    fun `invoke should call save on repository with correct userModel`() = runTest {
        val userToSave = UserModel(userId = "1", name = "Test User")
        coEvery { repository.save(any()) } returns 1

        val result = useCase(userToSave)

        assertEquals(1, result)
        coVerify(exactly = 1) { repository.save(userToSave) }
    }
}