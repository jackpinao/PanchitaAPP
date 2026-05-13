package com.pinao.panchitaapp.domain.usecase.Auth

import com.pinao.panchitaapp.domain.repository.AuthRepository

class IsUserLoggedInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}