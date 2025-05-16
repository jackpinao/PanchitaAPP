package com.pinao.panchitaapp.presentation.ui.clarorecarga

import com.pinao.panchitaapp.domain.model.RechangeModel

sealed interface RechangeUiState {
    object Loading : RechangeUiState
    data class Error(val throwable: Throwable): RechangeUiState
    data class Success(val rechangeModelList: List<RechangeModel>): RechangeUiState
}