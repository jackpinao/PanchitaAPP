package com.pinao.panchitaapp.domain.usecase.Auth

import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInUseCaseTest {

    private lateinit var useCase: SignInUseCase
    private val repository: AuthRepository = mockk()

    @Before
    fun setup() {
        useCase = SignInUseCase(repository)
    }

    @Test
    fun `invoke should return Success Result with UserModel from repository`() = runTest {
        val email = "test@test.com"
        val password = "password123"
        val expectedUser = UserModel(userId = "user_1", email = email, name = "Juan")
        val expectedResult = Result.success(expectedUser)

        coEvery { repository.signIn(email, password) } returns expectedResult

        val result = useCase(email, password)

        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        
        coVerify(exactly = 1) { repository.signIn(email, password) }
    }

    @Test
    fun `invoke should return Failure Result when repository throws error`() = runTest {
        val email = "test@test.com"
        val password = "wrong_password"
        val expectedException = Exception("Invalid credentials")
        val expectedResult = Result.failure<UserModel>(expectedException)

        coEvery { repository.signIn(email, password) } returns expectedResult

        val result = useCase(email, password)

        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
        
        coVerify(exactly = 1) { repository.signIn(email, password) }
    }
}