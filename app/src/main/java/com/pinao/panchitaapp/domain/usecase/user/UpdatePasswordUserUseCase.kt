package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.repository.UserRepository
import javax.inject.Inject

class UpdatePasswordUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Int, password: String) =
        userRepository.updatePassword(id, password)

}