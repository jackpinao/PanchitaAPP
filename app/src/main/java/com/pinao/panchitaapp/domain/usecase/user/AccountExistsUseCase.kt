package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.repository.UserRepository
import javax.inject.Inject

class AccountExistsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() =
        userRepository.accountExists()

}