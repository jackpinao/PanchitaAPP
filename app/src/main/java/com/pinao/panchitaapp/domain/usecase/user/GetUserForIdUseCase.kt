package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.repository.UserRepository

class GetUserForIdUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Int) =
        userRepository.getUserForId(id)

}