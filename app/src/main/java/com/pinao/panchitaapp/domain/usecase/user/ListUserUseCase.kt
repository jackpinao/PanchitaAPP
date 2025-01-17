package com.pinao.panchitaapp.domain.usecase.user

import com.pinao.panchitaapp.domain.model.User
import com.pinao.panchitaapp.domain.repository.UserRepository
import javax.inject.Inject

class ListUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(data: String) =
        userRepository.listDate(data)

}