package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        userRepository.getUser(email, password)

}