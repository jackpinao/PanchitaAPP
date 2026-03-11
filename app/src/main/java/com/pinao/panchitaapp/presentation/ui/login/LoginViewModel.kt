package com.pinao.panchitaapp.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.usecase.Auth.AuthUseCase
import com.pinao.panchitaapp.domain.usecase.products.RefreshProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lógica de autenticación y el estado reactivo de la UI de Login.
 */
class LoginViewModel(
    private val authUseCase: AuthUseCase,
    private val refreshProductsUseCase: RefreshProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(
        LoginUiState.Idle(UserModel())
    )
    val uiState = _uiState.asStateFlow()

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        if (authUseCase.isUserLoggedInUseCase()) {
            _uiState.value = LoginUiState.Success(UserModel())
            syncData()
        }
    }
    
    private fun syncData(){
        viewModelScope.launch {
            try {
                refreshProductsUseCase()
                Log.d("LoginViewModel", "Sincronización de datos completada")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error al sincronizar datos", e)
            }
        
        }
    }

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
                    is LoginUiState.Syncing -> currentState.copy(user = updatedUser)
                    is LoginUiState.Success -> currentState.copy(user = updatedUser)
                }
            })
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
                        // Cambiamos al estado de Sincronización
                        _uiState.value = LoginUiState.Syncing(authenticatedUser)
                        
                        // Realizamos la sincronización
                        refreshProductsUseCase()
                        
                        // Una vez terminada, pasamos a Success para que la UI navegue
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

    fun logout() {
        authUseCase.signOutUseCase()
        _uiState.value = LoginUiState.Idle(UserModel())
    }
}
