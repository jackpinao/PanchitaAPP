package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.repository.UserRepository

class UpdatePasswordUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Int, password: String) =
        userRepository.updatePassword(id, password)

}