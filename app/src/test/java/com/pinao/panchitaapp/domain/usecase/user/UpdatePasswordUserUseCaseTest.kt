package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.repository.UserRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdatePasswordUserUseCaseTest {

    private lateinit var useCase: UpdatePasswordUserUseCase
    private val repository: UserRepository = mockk()

    @Before
    fun setup() {
        useCase = UpdatePasswordUserUseCase(repository)
    }

    @Test
    fun `invoke should do nothing currently since updatePassword is commented out`() = runTest {
        val id = "user_123"
        val newPassword = "new_secure_password"
        
        // El caso de uso actual no retorna nada ni llama a ningun metodo del repositorio porque esta comentado.
        // Solo verificamos que se pueda invocar sin lanzar excepciones.
        useCase(id, newPassword)
        
        // Si en un futuro descomentas `userRepository.updatePassword(id, password)`, 
        // puedes agregar los mocks y coVerify(exactly = 1) aqui.
    }
}