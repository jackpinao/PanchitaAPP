package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.repository.UserRepository

class AccountExistsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() =
        userRepository.accountExists()

}