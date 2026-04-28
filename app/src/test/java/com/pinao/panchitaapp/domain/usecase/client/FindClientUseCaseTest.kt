package com.pinao.panchitaapp.domain.usecase.client

import com.pinao.panchitaapp.domain.repository.ClientRepository
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class FindClientUseCaseTest {

    private lateinit var useCase: FindClientUseCase
    private val repository: ClientRepository = mockk()

    @Before
    fun setup() {
        useCase = FindClientUseCase(repository)
    }

    @Test
    fun `use case should be instantiated with repository`() {
        assertNotNull(useCase)
    }
}
