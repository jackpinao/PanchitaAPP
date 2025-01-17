package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.model.User
import com.pinao.panchitaapp.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) =
        userRepository.save(user)

}