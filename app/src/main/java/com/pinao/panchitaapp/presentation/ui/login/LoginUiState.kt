package com.pinao.panchitaapp.presentation.ui.login

import androidx.annotation.StringRes
import com.pinao.panchitaapp.domain.model.UserModel

sealed interface LoginUiState {
    val user: UserModel
    data class Idle(override val user: UserModel) : LoginUiState
    data class Loading(override val user: UserModel) : LoginUiState
    data class Error(
        override val user: UserModel,
        val message: UiText
    ) : LoginUiState
    data class Success(override val user: UserModel) : LoginUiState
}

sealed class UiText{
    data class DynamicString(val value: String): UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ): UiText()
}