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

/**
 * ViewModel que gestiona la lógica de autenticación y el estado reactivo de la UI de Login.
 */
class LoginViewModel(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(
        LoginUiState.Idle(UserModel())
    )
    val uiState = _uiState.asStateFlow()

    /**
     * Actualiza el modelo de usuario de forma atómica.
     * Si el estado actual es [LoginUiState.Error], cambia automáticamente a [LoginUiState.Idle]
     * para limpiar los mensajes de error mientras el usuario edita los campos.
     */
    private fun updateState(reduce: (UserModel) -> UserModel) {
        _uiState.update { currentState ->
            val updatedUser = reduce(currentState.user)

            (if (currentState is LoginUiState.Error) {
                LoginUiState.Idle(user = updatedUser)
            } else {
                when (currentState) {
                    is LoginUiState.Idle -> currentState.copy(user = updatedUser)
                    is LoginUiState.Loading -> currentState.copy(user = updatedUser)
                    is LoginUiState.Success -> currentState.copy(user = updatedUser)
                    else -> {}
                }
            }) as LoginUiState
        }
    }

    fun onEmailChange(email: String) = updateState { it.copy(email = email) }

    fun onPasswordChange(password: String) = updateState { it.copy(password = password) }

    /**
     * Ejecuta el proceso de inicio de sesión tras validar que los campos no estén vacíos.
     */
    fun login() {
        val user = _uiState.value.user

        if (user.email.isBlank() || user.password.isBlank()) {
            _uiState.value = LoginUiState.Error(
                user = user,
                message = UiText.StringResource(R.string.error_empty_fields)
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading(user)
            
            try {
                authUseCase.signInUseCase(user.email, user.password)
                    .onSuccess { authenticatedUser ->
                        _uiState.value = LoginUiState.Success(authenticatedUser)
                    }
                    .onFailure { exception ->
                        _uiState.value = LoginUiState.Error(
                            user = user,
                            message = exception.message?.let { UiText.DynamicString(it) } 
                                ?: UiText.StringResource(R.string.error_unknown)
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    user = user,
                    message = e.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.error_unknown)
                )
            }
        }
    }
}
