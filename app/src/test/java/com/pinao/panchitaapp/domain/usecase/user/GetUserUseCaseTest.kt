package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetUserUseCaseTest {

    private lateinit var useCase: GetUserUseCase
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        useCase = GetUserUseCase(repository)
    }

    @Test
    fun `invoke should return UserModel when email exists`() = runTest {
        val email = "test@test.com"
        val expectedUser = UserModel(userId = "1", email = email, name = "Test User")
        coEvery { repository.getUser(email) } returns expectedUser

        val result = useCase(email)

        assertEquals(expectedUser, result)
        coVerify(exactly = 1) { repository.getUser(email) }
    }

    @Test
    fun `invoke should return null when email does not exist`() = runTest {
        val email = "wrong@test.com"
        coEvery { repository.getUser(email) } returns null

        val result = useCase(email)

        assertNull(result)
        coVerify(exactly = 1) { repository.getUser(email) }
    }
}