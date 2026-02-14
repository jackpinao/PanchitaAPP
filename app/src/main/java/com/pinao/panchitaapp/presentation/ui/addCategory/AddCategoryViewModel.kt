package com.pinao.panchitaapp.presentation.ui.addCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.usecase.category.CheckCategoryNameUseCase
import com.pinao.panchitaapp.domain.usecase.category.SaveCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
/**
 * ViewModel para gestionar la lógica de crear una nueva Categoría.
 */
class AddCategoryViewModel(
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val checkCategoryNameUseCase: CheckCategoryNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddCategoryUiState>(
        AddCategoryUiState.Idle(CategoryModel())
    )
    val uiState = _uiState.asStateFlow()

    private fun updateCategory(reduce: (CategoryModel) -> CategoryModel) {
        _uiState.update { cureentState ->
            when (cureentState) {
                is AddCategoryUiState.Idle -> cureentState.copy( reduce(cureentState.category))
                is AddCategoryUiState.Error -> cureentState.copy( reduce(cureentState.category))
                else -> cureentState
            }
        }
    }

    fun onNameChange(newName: String) {
        updateCategory { it.copy(name = newName) }
    }

    fun onRevenueChange(newRevenue: String) {
        // Validación básica para permitir solo números y un punto decimal
        if (newRevenue.isEmpty() || newRevenue.matches(Regex("^\\d*\\.?\\d*$"))) {
            updateCategory { it.copy(revenue = newRevenue.toDoubleOrNull() ?: 0.0) }
        }
    }

    fun saveCategory() {
        val currentState = _uiState.value

        val category = when (currentState) {
            is AddCategoryUiState.Idle -> currentState.category
            is AddCategoryUiState.Error -> currentState.category
            else -> return
        }

        viewModelScope.launch {
            _uiState.value = AddCategoryUiState.Loading(category)

            if (category.name.isBlank()) {
                _uiState.value = AddCategoryUiState.Error(category, "El nombre no puede estar vacío")
                return@launch
            }

            val nameExists = checkCategoryNameUseCase(category.name)
            if (nameExists) {
                _uiState.value = AddCategoryUiState.Error(category, "La categoría ya existe")
                return@launch
            }

            try {
                saveCategoryUseCase(
                    CategoryModel(
                        categoryId = UUID.randomUUID().toString(),
                        name = category.name,
                        revenue = category.revenue,
                    )
                )
                _uiState.value = AddCategoryUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddCategoryUiState.Error(category, "Error al guardar: ${e.message}")
            }
        }

    }

}