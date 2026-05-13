package com.pinao.panchitaapp.domain.usecase.Auth

import com.pinao.panchitaapp.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify

class IsUserLoggedInUseCaseTest {

    private lateinit var useCase: IsUserLoggedInUseCase
    private val repository: AuthRepository = mockk()

    @Before
    fun setup() {
        useCase = IsUserLoggedInUseCase(repository)
    }

    @Test
    fun `invoke should return true when repository says user is logged in`() = runTest {
        coEvery { repository.isUserLoggedIn() } returns true

        val result = useCase()

        assertTrue(result)
        coVerify(exactly = 1) { repository.isUserLoggedIn() }
    }

    @Test
    fun `invoke should return false when repository says user is not logged in`() = runTest {
        coEvery { repository.isUserLoggedIn() } returns false

        val result = useCase()

        assertFalse(result)
        coVerify(exactly = 1) { repository.isUserLoggedIn() }
    }
}