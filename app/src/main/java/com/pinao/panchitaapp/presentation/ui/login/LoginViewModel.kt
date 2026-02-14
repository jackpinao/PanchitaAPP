package com.pinao.panchitaapp.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.usecase.Auth.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(
        LoginUiState.Idle(UserModel())
    )
    val uiState = _uiState.asStateFlow()

    private fun updateState(reduce: (UserModel) -> UserModel) {
        _uiState.update { currentState ->
            val updatedUser = reduce(currentState.user)
            when (currentState) {
                is LoginUiState.Idle -> currentState.copy(updatedUser)
                is LoginUiState.Error -> currentState.copy(updatedUser)
                is LoginUiState.Loading -> currentState.copy(updatedUser)
                is LoginUiState.Success -> currentState.copy(updatedUser)
            }
        }
    }

    fun onEmailChange(email: String) = updateState { it.copy(email = email) }

    fun onPasswordChange(password: String) = updateState { it.copy(password = password) }

    /**
     * Ejecuta el proceso de autenticación.
     */
    fun login() {

        val user = _uiState.value.user

        if (user.email.isBlank() || user.password.isBlank()) {
            _uiState.value = LoginUiState.Error(
                user,
                message = UiText.StringResource(R.string.error_empty_fields)
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading(user)

            try {
                authUseCase.signInUseCase(user.email, user.password)
                _uiState.value = LoginUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    user = user,
                    message = UiText.DynamicString(e.message ?: "Error desconocido")
                )
            }
        }
    }
}
