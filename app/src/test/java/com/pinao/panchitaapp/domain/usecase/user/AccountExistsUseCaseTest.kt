package com.pinao.panchitaapp.domain.usecase.user

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
class AccountExistsUseCaseTest {

    private lateinit var useCase: AccountExistsUseCase
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        useCase = AccountExistsUseCase(repository)
    }

    @Test
    fun `invoke should return result of accountExists from repository`() = runTest {
        val expectedResult = 1
        coEvery { repository.accountExists() } returns expectedResult

        val result = useCase()

        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.accountExists() }
    }
}