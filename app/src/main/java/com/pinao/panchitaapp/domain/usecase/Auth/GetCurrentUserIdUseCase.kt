package com.pinao.panchitaapp.domain.usecase.Auth

import com.pinao.panchitaapp.domain.repository.AuthRepository

class GetCurrentUserIdUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): String = authRepository.getCurrentUserId()
}
