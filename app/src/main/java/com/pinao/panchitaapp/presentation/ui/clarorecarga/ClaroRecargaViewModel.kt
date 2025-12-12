package com.pinao.panchitaapp.presentation.ui.clarorecarga

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.RechangeModel
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
import com.pinao.panchitaapp.presentation.ui.clarorecarga.RechangeUiState.Error
import com.pinao.panchitaapp.presentation.ui.clarorecarga.RechangeUiState.Loading
import com.pinao.panchitaapp.presentation.ui.clarorecarga.RechangeUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

//@RequiresApi(Build.VERSION_CODES.O)
class ClaroRecargaViewModel(
    private val saveRechangeUseCase: SaveRechangeUseCase,
    private val getListForDateRechangeUC: GetListForDateRechangeUC,
    getAllDateRechangeUseCase: GetAllDateRechangeUseCase
) : ViewModel() {

    private val _dateFilterRechanges = MutableStateFlow<List<RechangeModel>>(emptyList())
    val dateFilterRechanges: StateFlow<List<RechangeModel>> = _dateFilterRechanges.asStateFlow()

    private val _uiState = MutableStateFlow<RechangeUiState>(Loading)
    val uiState: StateFlow<RechangeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllDateRechangeUseCase()
                .onStart { _uiState.value = Loading }
                .catch { exception -> _uiState.value = Error(exception) }
                .collect { rechanges -> _uiState.value = Success(rechanges) }
        }
    }

    fun getForDateRechange(date: String) {
        viewModelScope.launch {
            getListForDateRechangeUC(date)
                .onStart { _uiState.value = Loading }
                .catch { exception ->
                    _uiState.value = Error(exception)
                    _dateFilterRechanges.value = emptyList()
                }
                .collect { rechanges ->
                    _uiState.value = Success(rechanges)
                    _dateFilterRechanges.value = rechanges
                }
        }
    }

    fun updateRechange(rechangeModel: RechangeModel) {
        viewModelScope.launch {
            try {
                _uiState.value = Loading
                saveRechangeUseCase(rechangeModel)
                _uiState.value = Success(listOf(rechangeModel))
            } catch (e: Exception) {
                _uiState.value = Error(e)
            }
        }
    }

}

