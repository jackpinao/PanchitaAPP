package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userModel: UserModel) = userRepository.delete(userModel)
}