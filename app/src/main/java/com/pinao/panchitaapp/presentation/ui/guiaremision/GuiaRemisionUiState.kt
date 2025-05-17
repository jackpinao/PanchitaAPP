package com.pinao.panchitaapp.presentation.ui.guiaremision

import com.pinao.panchitaapp.domain.model.GuiaRemisionModel

sealed interface GuiaRemisionUiState {
    object Loading : GuiaRemisionUiState
    data class Error(val throwable: Throwable): GuiaRemisionUiState
    data class Success(val guiaRemisionModelList: List<GuiaRemisionModel>): GuiaRemisionUiState
}