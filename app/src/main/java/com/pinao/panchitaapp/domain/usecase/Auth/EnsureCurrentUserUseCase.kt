package com.pinao.panchitaapp.domain.usecase.Auth

import com.pinao.panchitaapp.domain.repository.AuthRepository

class EnsureCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String = authRepository.ensureCurrentUserInRoom()
}
