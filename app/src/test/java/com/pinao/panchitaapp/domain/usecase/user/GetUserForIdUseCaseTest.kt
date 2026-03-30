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
class GetUserForIdUseCaseTest {

    private lateinit var useCase: GetUserForIdUseCase
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        useCase = GetUserForIdUseCase(repository)
    }

    @Test
    fun `invoke should return UserModel from repository when id exists`() = runTest {
        val testId = "uid_123"
        val expectedUser = UserModel(userId = testId, name = "Test User")
        coEvery { repository.getUserForId(testId) } returns expectedUser

        val result = useCase(testId)

        assertEquals(expectedUser, result)
        coVerify(exactly = 1) { repository.getUserForId(testId) }
    }

    @Test
    fun `invoke should return null when id does not exist`() = runTest {
        val testId = "invalid_id"
        coEvery { repository.getUserForId(testId) } returns null

        val result = useCase(testId)

        assertNull(result)
        coVerify(exactly = 1) { repository.getUserForId(testId) }
    }
}