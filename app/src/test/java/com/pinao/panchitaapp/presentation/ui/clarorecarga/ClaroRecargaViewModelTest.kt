package com.pinao.panchitaapp.presentation.ui.clarorecarga

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.RechangeModel
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClaroRecargaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val saveRechangeUseCase: SaveRechangeUseCase = mockk()
    private val getListForDateRechangeUC: GetListForDateRechangeUC = mockk()
    private val getAllDateRechangeUseCase: GetAllDateRechangeUseCase = mockk()

    private lateinit var viewModel: ClaroRecargaViewModel

    private val mockRechange1 = mockk<RechangeModel>()
    private val mockRechange2 = mockk<RechangeModel>()
    private val mockList = listOf(mockRechange1, mockRechange2)

    @Before
    fun setup() {
        // Mocking default behavior para el init block del ViewModel
        every { getAllDateRechangeUseCase() } returns flowOf(mockList)
    }

    @Test
    fun `init should fetch all rechanges and update uiState to Success`() = runTest {
        viewModel = ClaroRecargaViewModel(
            saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase
        )

        viewModel.uiState.test {
            // El estado inicial debe ser Loading (desde el init del StateFlow)
            val initialState = awaitItem()
            assertTrue(initialState is RechangeUiState.Loading)

            // Damos paso para que viewModelScope.launch ejecute la corrutina
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            // Recibimos Success de la llamada de getAllDateRechangeUseCase()
            val successState = awaitItem()
            assertTrue(successState is RechangeUiState.Success)
            assertEquals(mockList, (successState as RechangeUiState.Success).rechangeModelList)
        }

        coVerify(exactly = 1) { getAllDateRechangeUseCase() }
    }

    @Test
    fun `getForDateRechange should filter rechanges by date and emit Success`() = runTest {
        val testDate = "2023-10-01"
        val filteredList = listOf(mockRechange1)
        every { getListForDateRechangeUC(testDate) } returns flowOf(filteredList)

        viewModel = ClaroRecargaViewModel(
            saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase
        )

        // Dejamos que el bloque init termine completamente antes de lanzar nuestra acción
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Saltamos el valor actual (Success del init block)
            awaitItem()

            // Ejecutamos el caso a probar
            viewModel.getForDateRechange(testDate)

            // Emite estado Loading por el onStart{}
            val loadingState = awaitItem()
            assertTrue(loadingState is RechangeUiState.Loading)

            // Damos paso a la corrutina de getForDateRechange
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            // Emite estado Success
            val successState = awaitItem()
            assertTrue(successState is RechangeUiState.Success)
            assertEquals(filteredList, (successState as RechangeUiState.Success).rechangeModelList)
        }

        // Además verificamos que _dateFilterRechanges se haya llenado
        assertEquals(filteredList, viewModel.dateFilterRechanges.value)
        coVerify { getListForDateRechangeUC(testDate) }
    }

    @Test
    fun `getForDateRechange should handle error and clear filter`() = runTest {
        val testDate = "2023-10-02"
        val exception = RuntimeException("Error fetching dates")
        every { getListForDateRechangeUC(testDate) } returns flow { throw exception }

        viewModel = ClaroRecargaViewModel(
            saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase
        )

        // Dejamos que el init termine
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Actual estado: Success (init)

            viewModel.getForDateRechange(testDate)

            assertTrue(awaitItem() is RechangeUiState.Loading) // del onStart{}

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is RechangeUiState.Error)
            assertEquals(exception, (errorState as RechangeUiState.Error).throwable)
        }

        // El filtro de fechas debió limpiarse ante una excepción
        assertTrue(viewModel.dateFilterRechanges.value.isEmpty())
        coVerify { getListForDateRechangeUC(testDate) }
    }

    @Test
    fun `updateRechange should save successfully and emit Success`() = runTest {
        coEvery { saveRechangeUseCase(mockRechange1) } returns Unit

        viewModel = ClaroRecargaViewModel(
            saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase
        )

        // Permitir que init pase
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // actual estado

            viewModel.updateRechange(mockRechange1)

            assertTrue(awaitItem() is RechangeUiState.Loading)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is RechangeUiState.Success)
            // Cuando es update unitario, el ViewModel re-envia un List de 1 elemento
            assertEquals(listOf(mockRechange1), (successState as RechangeUiState.Success).rechangeModelList)
        }

        coVerify { saveRechangeUseCase(mockRechange1) }
    }

    @Test
    fun `updateRechange should emit Error state when exception occurs`() = runTest {
        val exception = Exception("Save failed internally")
        coEvery { saveRechangeUseCase(mockRechange1) } throws exception

        viewModel = ClaroRecargaViewModel(
            saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase
        )

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Actual estado: Success

            viewModel.updateRechange(mockRechange1)

            assertTrue(awaitItem() is RechangeUiState.Loading)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is RechangeUiState.Error)
            assertEquals(exception, (errorState as RechangeUiState.Error).throwable)
        }

        coVerify { saveRechangeUseCase(mockRechange1) }
    }
}
