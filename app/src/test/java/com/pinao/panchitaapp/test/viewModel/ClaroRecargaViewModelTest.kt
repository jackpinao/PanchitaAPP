package com.pinao.panchitaapp.test.viewModel

import com.pinao.panchitaapp.domain.model.RechangeModel
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ClaroRecargaViewModelTest {

    private lateinit var getListForDateRechangeUC: GetListForDateRechangeUC
    private lateinit var saveRechangeUseCase: SaveRechangeUseCase
    private lateinit var getAllDateRechangeUseCase: GetAllDateRechangeUseCase
    private lateinit var claroRecargaViewModel: ClaroRecargaViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getListForDateRechangeUC = mock(GetListForDateRechangeUC::class.java)
        saveRechangeUseCase = mock(SaveRechangeUseCase::class.java)
        getAllDateRechangeUseCase = mock(GetAllDateRechangeUseCase::class.java)

        claroRecargaViewModel = ClaroRecargaViewModel(
            getListForDateRechangeUC = getListForDateRechangeUC,
            saveRechangeUseCase = saveRechangeUseCase,
            getAllDateRechangeUseCase = getAllDateRechangeUseCase
        )
    }

    @Test
    fun getForDateRechange_returnsFilteredRechanges() = runTest {
        val date = "2023-10-01"
        val rechangeList = listOf(
            RechangeModel(date = "2023-10-01", amount = 100, numPhone = "123456789"),
            RechangeModel(date = "2023-10-01", amount = 200, numPhone = "987654321")
        )
        `when`(getListForDateRechangeUC(date)).thenReturn(flowOf(rechangeList))

        claroRecargaViewModel.getForDateRechange(date)

        val result = mutableListOf<List<RechangeModel>>()
        //assertEquals(rechangeList, result)
        claroRecargaViewModel.dateFilterRechanges
            .take(1)
            .collect { emittedValue ->
            result.add(emittedValue)
        }
        assertEquals(rechangeList, result.first())
    }

    @Test
    fun getForDateRechange_withNoRechanges_returnsEmptyList() = runTest {
        val date = "2023-10-01"
        `when`(getListForDateRechangeUC(date)).thenReturn(flowOf(emptyList()))

        claroRecargaViewModel.getForDateRechange(date)

        val result = claroRecargaViewModel.dateFilterRechanges.value
        assertTrue(result.isEmpty())
    }

    @Test
    fun getForDateRechange_withError_returnsEmptyList() = runTest {
        val date = "2023-10-01"
        `when`(getListForDateRechangeUC(date)).thenReturn(flow { throw Exception("Error") })

        claroRecargaViewModel.getForDateRechange(date)

        assertTrue(claroRecargaViewModel.dateFilterRechanges.value.isEmpty())
    }

    @Test
    fun updateRechange_savesRechangeSuccessfully() = runTest {
        val rechangeModel = RechangeModel(date = "2023-10-10", amount = 100, numPhone = "123456789")
        doAnswer {}.`when`(saveRechangeUseCase).invoke(rechangeModel)

        claroRecargaViewModel.updateRechange(rechangeModel)

        verify(saveRechangeUseCase).invoke(rechangeModel)
    }
}