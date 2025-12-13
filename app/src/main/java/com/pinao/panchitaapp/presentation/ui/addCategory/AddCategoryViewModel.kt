package com.pinao.panchitaapp.presentation.ui.addCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.usecase.category.CheckCategoryNameUseCase
import com.pinao.panchitaapp.domain.usecase.category.SaveCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Estado de la UI para la pantalla de Añadir Categoría.
 */
data class AddCategoryUiState(
    val categoryName: String = "",
    val categoryRevenue: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateBack: Boolean = false
)

/**
 * ViewModel para gestionar la lógica de crear una nueva Categoría.
 */
class AddCategoryViewModel(
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val checkCategoryNameUseCase: CheckCategoryNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCategoryUiState())
    val uiState: StateFlow<AddCategoryUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(categoryName = name, error = null) } // Limpiar error al escribir
    }

    fun onRevenueChange(revenue: String) {
        // Validación básica para permitir solo números y un punto decimal
        if (revenue.isEmpty() || revenue.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(categoryRevenue = revenue) }
        }
    }

    fun saveCategory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentState = _uiState.value

            // 1. Validar que el nombre no esté vacío
            if (currentState.categoryName.isBlank()) {
                _uiState.update { it.copy(error = "El nombre no puede estar vacío", isLoading = false) }
                return@launch
            }

            // 2. Validar que el nombre sea único
            val nameExists = checkCategoryNameUseCase(currentState.categoryName)
            if (nameExists) {
                _uiState.update { it.copy(error = "La categoría ya existe", isLoading = false) }
                return@launch
            }

            // 3. Si las validaciones pasan, guardar la categoría
            try {
                val categoryToSave = CategoryModel(
                    id = UUID.randomUUID().toString(), // Generamos un nuevo ID único
                    name = currentState.categoryName,
                    revenue = currentState.categoryRevenue.toDoubleOrNull() ?: 0.0
                )

                saveCategoryUseCase(categoryToSave)
                _uiState.update { it.copy(isLoading = false, navigateBack = true) } // Señal para navegar hacia atrás

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}", isLoading = false) }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }
}