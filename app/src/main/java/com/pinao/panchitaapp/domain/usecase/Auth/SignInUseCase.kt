package com.pinao.panchitaapp.domain.usecase.Auth

import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String): Result<UserModel> {
        return authRepository.signIn(email, pass)
    }
}