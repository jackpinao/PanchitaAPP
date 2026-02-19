package com.pinao.panchitaapp.presentation.ui.addCategory

import androidx.annotation.StringRes
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.presentation.ui.login.UiText

sealed interface AddCategoryUiState {
    val category: CategoryModel

    // 1. Estado Inicial / Editando
    data class Idle(override val category: CategoryModel) : AddCategoryUiState

    // 2. Estado de Carga (bloqueamos el botón de guardar)
    // Pasamos los datos actuales para que la UI no se quede en blanco
    data class Loading(override val category: CategoryModel) : AddCategoryUiState

    // 3. Estado de Error
    data class Error(
        override val category: CategoryModel,
        val message: UiText
    ) : AddCategoryUiState

    // 4. Estado de Éxito (Disparamos la navegación)
    data class Success(override val category: CategoryModel) : AddCategoryUiState
}
