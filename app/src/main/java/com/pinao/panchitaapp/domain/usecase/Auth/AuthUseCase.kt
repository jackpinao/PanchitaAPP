package com.pinao.panchitaapp.domain.usecase.Auth

data class AuthUseCase (
    val signInUseCase: SignInUseCase,
    val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    val signOutUseCase: SignOutUseCase,
    val ensureCurrentUserUseCase: EnsureCurrentUserUseCase
)
