package com.pinao.panchitaapp.presentation.ui.clarorecarga

import com.pinao.panchitaapp.domain.model.Rechange

sealed interface RechangeUiState {
    object Loading : RechangeUiState
    data class Error(val throwable: Throwable): RechangeUiState
    data class Success(val rechangeList: List<Rechange>): RechangeUiState
}