package com.pinao.panchitaapp.domain.usecase.Auth

import com.pinao.panchitaapp.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SignOutUseCaseTest {

    private lateinit var useCase: SignOutUseCase
    private val repository: AuthRepository = mockk()

    @Before
    fun setup() {
        useCase = SignOutUseCase(repository)
    }

    @Test
    fun `invoke should call signOut on repository`() {
        every { repository.signOut() } returns Unit

        useCase()

        verify(exactly = 1) { repository.signOut() }
    }
}