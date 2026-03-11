package com.pinao.panchitaapp.presentation.ui.login

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pinao.panchitaapp.domain.model.UserModel

sealed interface LoginUiState {
    val user: UserModel

    data class Idle(override val user: UserModel) : LoginUiState
    data class Loading(override val user: UserModel) : LoginUiState
    data class Syncing(override val user: UserModel) : LoginUiState
    data class Error(
        override val user: UserModel,
        val message: UiText
    ) : LoginUiState

    data class Success(override val user: UserModel) : LoginUiState
}

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(
        @param:StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    @Composable
    @Suppress("UNUSED")
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}