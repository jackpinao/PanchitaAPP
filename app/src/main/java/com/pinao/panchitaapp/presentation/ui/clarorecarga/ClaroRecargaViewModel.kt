package com.pinao.panchitaapp.presentation.ui.clarorecarga

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.presentation.common.GetCurrentDateTime
//import com.pinao.panchitaapp.presentation.ui.clarorecarga.RechangeUiState.Success
//import com.pinao.panchitaapp.presentation.ui.clarorecarga.RechangeUiState.Error
//import com.pinao.panchitaapp.presentation.ui.clarorecarga.RechangeUiState.Loading
import com.pinao.panchitaapp.presentation.ui.clarorecarga.RechangeUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ClaroRecargaViewModel @Inject constructor(
    private val saveRechangeUseCase: SaveRechangeUseCase,
    private val getListForDateRechangeUC: GetListForDateRechangeUC,
    getAllDateRechangeUseCase: GetAllDateRechangeUseCase
) : ViewModel() {

    private val dateToday = GetCurrentDateTime().getCurrentDateTime2()

    val uiState: StateFlow<RechangeUiState> = getListForDateRechangeUC(dateToday).map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)


//    val uiState: StateFlow<RechangeUiState> = getAllDateRechangeUseCase().map(::Success)
//        .catch { Error(it) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    private val _dateFilterRechanges = MutableStateFlow<List<Rechange>>(emptyList())
    val dateFilterRechanges: StateFlow<List<Rechange>> = _dateFilterRechanges.asStateFlow()

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

//    var state by mutableStateOf(UiState())
//        private set

//    init {
//        viewModelScope.launch {
//            getRechangeUseCase(dateToday).collect(
//                println("$it")
//            )
//        }
//    }

    fun getForDateRechange(date: String) {
        viewModelScope.launch {
            getListForDateRechangeUC(date)
                .catch { _dateFilterRechanges.value = emptyList() }
                .collect { rechanges -> _dateFilterRechanges.value = rechanges }
        }
    }

    fun updateRechange(rechange: Rechange) {
        viewModelScope.launch {
//            _state.value = _state.value.copy(rechange = rechange)
            //saveRechangeUseCase.invoke(rechange)
            saveRechangeUseCase(rechange)
        }
    }

    data class UiState(
        val rechange: Rechange? = null,
        val listRechange: List<Rechange> = emptyList(),
        val isLoading: Boolean = false,
        val error: String = ""
    )

}

