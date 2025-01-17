package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.model.User
import com.pinao.panchitaapp.domain.repository.UserRepository
import javax.inject.Inject

class SaveAccountUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) =
        userRepository.saveAccount(user)

}