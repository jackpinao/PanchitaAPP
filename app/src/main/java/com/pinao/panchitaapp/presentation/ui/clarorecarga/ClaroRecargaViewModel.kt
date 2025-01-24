package com.pinao.panchitaapp.presentation.ui.clarorecarga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClaroRecargaViewModel(
    private val saveRechangeUseCase: SaveRechangeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

//    init {
//        viewModelScope.launch{
//            _state.value = UiState(isLoading = true)
//            _state.value = UiState(rechange = Rechange())
//            saveRechangeUseCase.invoke(state.value.rechange!!)
//        }
//    }

    fun updateRechange(rechange: Rechange) {
        viewModelScope.launch {
            _state.value = _state.value.copy(rechange = rechange)
            saveRechangeUseCase.invoke(rechange)
        }

    }

    data class UiState(
        val rechange: Rechange? = null,
        val isLoading: Boolean = false
    )

}

