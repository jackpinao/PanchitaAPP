package com.pinao.panchitaapp.presentation.ui.addCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.usecase.category.CheckCategoryNameUseCase
import com.pinao.panchitaapp.domain.usecase.category.SaveCategoryUseCase
import com.pinao.panchitaapp.presentation.ui.login.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.util.UUID

/**
 * ViewModel para gestionar la lógica de crear una nueva Categoría.
 */
@KoinViewModel
class AddCategoryViewModel(
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val checkCategoryNameUseCase: CheckCategoryNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddCategoryUiState>(
        AddCategoryUiState.Idle(CategoryModel())
    )
    val uiState = _uiState.asStateFlow()

    private fun updateState(reduce: (CategoryModel) -> CategoryModel) {
        _uiState.update { cureentState ->
            val updatedCategory = reduce(cureentState.category)

            when (cureentState) {
                is AddCategoryUiState.Idle -> cureentState.copy(category = updatedCategory)
                is AddCategoryUiState.Loading -> cureentState.copy(category = updatedCategory)
                is AddCategoryUiState.Success -> cureentState.copy(category = updatedCategory)
                is AddCategoryUiState.Error -> AddCategoryUiState.Idle(category = updatedCategory)
            }
        }
    }

    fun onNameChange(newName: String) {
        updateState { it.copy(name = newName) }
    }

    fun onRevenueChange(newRevenue: String) {
        // Validación básica para permitir solo números y un punto decimal
        if (newRevenue.isEmpty() || newRevenue.matches(Regex("^\\d*\\.?\\d*$"))) {
            updateState { it.copy(revenue = newRevenue.toDoubleOrNull() ?: 0.0) }
        }
    }

    fun saveCategory() {
        val category = _uiState.value.category

        if (category.name.isBlank()) {
            _uiState.value = AddCategoryUiState.Error(
                category = category,
                message = UiText.StringResource(R.string.error_empty_fields)
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = AddCategoryUiState.Loading(category)

            val nameExists = checkCategoryNameUseCase(category.name)
            if (nameExists) {
                _uiState.value = AddCategoryUiState.Error(
                    category,
                    UiText.StringResource(R.string.error_category_name_exists)
                )
                return@launch
            }

            try {
                saveCategoryUseCase(
                    category.copy(categoryId = UUID.randomUUID().toString())
                )
                _uiState.value = AddCategoryUiState.Success(category)

            } catch (e: Exception) {
                _uiState.value = AddCategoryUiState.Error(
                    category = category,
                    message = e.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.error_unknown)
                )
                throw Exception("Error al guardar: ${e.message}")
            }
        }
    }
}