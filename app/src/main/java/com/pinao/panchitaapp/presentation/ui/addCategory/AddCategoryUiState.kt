package com.pinao.panchitaapp.presentation.ui.addCategory

import com.pinao.panchitaapp.domain.model.CategoryModel

sealed interface AddCategoryUiState {
    // 1. Estado Inicial / Editando
    data class Idle(val category: CategoryModel) : AddCategoryUiState

    // 2. Estado de Carga (bloqueamos el botón de guardar)
    // Pasamos los datos actuales para que la UI no se quede en blanco
    data class Loading(val category: CategoryModel) : AddCategoryUiState

    // 3. Estado de Error
    data class Error(val category: CategoryModel, val message: String) : AddCategoryUiState

    // 4. Estado de Éxito (Disparamos la navegación)
    object Success : AddCategoryUiState
}