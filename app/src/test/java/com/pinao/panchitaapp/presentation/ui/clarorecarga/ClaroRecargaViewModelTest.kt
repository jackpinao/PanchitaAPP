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
import io.mockk.verify
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

    private val mockRechanges = listOf(
        RechangeModel(id = "1", date = "2023-11-01", amount = 10, numPhone = "111222333"),
        RechangeModel(id = "2", date = "2023-11-02", amount = 20, numPhone = "444555666")
    )

    @Before
    fun setup() {
        // Mock default behavior for init
        every { getAllDateRechangeUseCase() } returns flowOf(mockRechanges)
    }

    @Test
    fun `init should load all rechanges into uiState`() = runTest {
        viewModel = ClaroRecargaViewModel(saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase)

        // Estabilizamos corrutinas de init
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val finalState = awaitItem()
            
            assertTrue(finalState is RechangeUiState.Success)
            val successState = finalState as RechangeUiState.Success
            assertEquals(2, successState.rechangeModelList.size)
            assertEquals("111222333", successState.rechangeModelList[0].numPhone)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { getAllDateRechangeUseCase() }
    }

    @Test
    fun `init should handle error state when flow throws exception`() = runTest {
        val errorFlow = flow<List<RechangeModel>> { throw Exception("Database error") }
        every { getAllDateRechangeUseCase() } returns errorFlow

        viewModel = ClaroRecargaViewModel(saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val finalState = awaitItem()
            
            assertTrue(finalState is RechangeUiState.Error)
            val errorState = finalState as RechangeUiState.Error
            assertEquals("Database error", errorState.throwable.message)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getForDateRechange should filter and update uiState and dateFilterRechanges`() = runTest {
        val targetDate = "2023-11-01"
        val filteredList = listOf(mockRechanges[0])
        every { getListForDateRechangeUC(targetDate) } returns flowOf(filteredList)

        viewModel = ClaroRecargaViewModel(saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem() // Estado cargado por init
            assertTrue(initialState is RechangeUiState.Success)

            // Act
            viewModel.getForDateRechange(targetDate)

            // Atrapamos loading emitido por onStart
            val loadingState = awaitItem()
            assertTrue(loadingState is RechangeUiState.Loading)
            
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            // Atrapamos Success
            val finalState = expectMostRecentItem()
            assertTrue(finalState is RechangeUiState.Success)
            assertEquals(1, (finalState as RechangeUiState.Success).rechangeModelList.size)

            cancelAndIgnoreRemainingEvents()
        }

        // Verificamos el stateflow paralelo
        assertEquals(1, viewModel.dateFilterRechanges.value.size)
        assertEquals(targetDate, viewModel.dateFilterRechanges.value[0].date)
        
        verify(exactly = 1) { getListForDateRechangeUC(targetDate) }
    }

    @Test
    fun `getForDateRechange should update uiState with Error on failure`() = runTest {
        val targetDate = "2023-11-99"
        val errorFlow = flow<List<RechangeModel>> { throw Exception("Invalid date") }
        every { getListForDateRechangeUC(targetDate) } returns errorFlow

        viewModel = ClaroRecargaViewModel(saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Estado init

            viewModel.getForDateRechange(targetDate)
            
            val loadingState = awaitItem()
            assertTrue(loadingState is RechangeUiState.Loading)
            
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = expectMostRecentItem()
            assertTrue(errorState is RechangeUiState.Error)
            assertEquals("Invalid date", (errorState as RechangeUiState.Error).throwable.message)

            cancelAndIgnoreRemainingEvents()
        }

        // Verifica que limpia el dateFilterRechanges en caso de error
        assertTrue(viewModel.dateFilterRechanges.value.isEmpty())
    }

    @Test
    fun `updateRechange should call save use case and update uiState`() = runTest {
        val newRechange = RechangeModel(id = "3", date = "2023-11-03", amount = 50, numPhone = "999888777")
        coEvery { saveRechangeUseCase(any()) } returns Unit

        viewModel = ClaroRecargaViewModel(saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Estado init

            viewModel.updateRechange(newRechange)

            // Loading emitido manualmente
            val loadingState = awaitItem()
            assertTrue(loadingState is RechangeUiState.Loading)
            
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            // Success emitido después de save
            val successState = expectMostRecentItem()
            assertTrue(successState is RechangeUiState.Success)
            val list = (successState as RechangeUiState.Success).rechangeModelList
            assertEquals(1, list.size)
            assertEquals("3", list[0].id)
            
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { saveRechangeUseCase(newRechange) }
    }

    @Test
    fun `updateRechange should handle Error if save use case fails`() = runTest {
        val newRechange = RechangeModel(id = "3", date = "2023-11-03", amount = 50, numPhone = "999888777")
        coEvery { saveRechangeUseCase(any()) } throws Exception("Save failed")

        viewModel = ClaroRecargaViewModel(saveRechangeUseCase, getListForDateRechangeUC, getAllDateRechangeUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Estado init

            viewModel.updateRechange(newRechange)

            val loadingState = awaitItem()
            assertTrue(loadingState is RechangeUiState.Loading)
            
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = expectMostRecentItem()
            assertTrue(errorState is RechangeUiState.Error)
            assertEquals("Save failed", (errorState as RechangeUiState.Error).throwable.message)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}