package com.pinao.panchitaapp.domain.usecase.Auth

import com.google.common.truth.Truth.assertThat
import com.pinao.panchitaapp.domain.repository.AuthRepository
import io.mockk.mockk
import org.junit.Test

class AuthUseCaseTest {

    private val authRepository: AuthRepository = mockk()

    @Test
    fun `AuthUseCase equals returns true for same property instances`() {
        val signIn = SignInUseCase(authRepository)
        val isLogged = IsUserLoggedInUseCase(authRepository)
        val signOut = SignOutUseCase(authRepository)

        val ensureUser = EnsureCurrentUserUseCase(authRepository)
        val bundle1 = AuthUseCase(signIn, isLogged, signOut, ensureUser)
        val bundle2 = AuthUseCase(signIn, isLogged, signOut, ensureUser)

        assertThat(bundle1).isEqualTo(bundle2)
    }

    @Test
    fun `AuthUseCase equals returns false for different property instances`() {
        val signIn1 = SignInUseCase(authRepository)
        val signIn2 = SignInUseCase(mockk())
        val isLogged = IsUserLoggedInUseCase(authRepository)
        val signOut = SignOutUseCase(authRepository)
        val ensureUser = EnsureCurrentUserUseCase(authRepository)

        val bundle1 = AuthUseCase(signIn1, isLogged, signOut, ensureUser)
        val bundle2 = AuthUseCase(signIn2, isLogged, signOut, ensureUser)

        assertThat(bundle1).isNotEqualTo(bundle2)
    }

    @Test
    fun `AuthUseCase equals returns false when compared to null`() {
        val bundle = AuthUseCase(
            SignInUseCase(authRepository),
            IsUserLoggedInUseCase(authRepository),
            SignOutUseCase(authRepository),
            EnsureCurrentUserUseCase(authRepository)
        )

        assertThat(bundle).isNotEqualTo(null)
    }

    @Test
    fun `AuthUseCase hashCode is consistent for equal instances`() {
        val signIn = SignInUseCase(authRepository)
        val isLogged = IsUserLoggedInUseCase(authRepository)
        val signOut = SignOutUseCase(authRepository)

        val ensureUser = EnsureCurrentUserUseCase(authRepository)
        val bundle1 = AuthUseCase(signIn, isLogged, signOut, ensureUser)
        val bundle2 = AuthUseCase(signIn, isLogged, signOut, ensureUser)

        assertThat(bundle1.hashCode()).isEqualTo(bundle2.hashCode())
    }
}
