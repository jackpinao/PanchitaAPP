package com.pinao.panchitaapp.presentation.ui.claroDetall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.domain.usecase.rechange.GetRechangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClaroDetallViewModel @Inject constructor(
    private val useCase: GetRechangeUseCase
): ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun getRechange(data: String) {
        viewModelScope.launch {
            useCase.invoke(data)
        }
    }
}

data class UiState(
    val rechange: Rechange? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)