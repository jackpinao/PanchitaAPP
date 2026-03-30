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
class SaveAccountUseCaseTest {

    private lateinit var useCase: SaveAccountUseCase
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        useCase = SaveAccountUseCase(repository)
    }

    @Test
    fun `invoke should call saveAccount on repository with correct userModel`() = runTest {
        val userToSave = UserModel(userId = "acc1", name = "Test Account")
        val savedAccount = UserModel(userId = "acc1", name = "Test Account Saved")
        
        coEvery { repository.saveAccount(any()) } returns savedAccount

        val result = useCase(userToSave)

        assertEquals(savedAccount, result)
        coVerify(exactly = 1) { repository.saveAccount(userToSave) }
    }
}