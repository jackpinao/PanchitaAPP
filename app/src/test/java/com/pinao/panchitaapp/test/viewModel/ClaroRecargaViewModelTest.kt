package com.pinao.panchitaapp.test.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ClaroRecargaViewModelTest {

    @RelaxedMockK
    private lateinit var saveRechangeUseCase: SaveRechangeUseCase

    private lateinit var claroRecargaViewModel: ClaroRecargaViewModel

    @get:Rule
    var rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        claroRecargaViewModel = ClaroRecargaViewModel(saveRechangeUseCase)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }

    @Test
    fun updateRechange_updatesStateWithNewRechange() = runTest {
        val rechange = Rechange(id = 1, amount = 100)
        claroRecargaViewModel.updateRechange(rechange)
        assertEquals(rechange, claroRecargaViewModel.state.value.rechange)
    }

    @Test
    fun updateRechange_callsSaveRechangeUseCase() = runTest {
        val rechange = Rechange(id = 1, amount = 100)
        coEvery { saveRechangeUseCase(rechange) } returns Unit
        claroRecargaViewModel.updateRechange(rechange)
        coVerify { saveRechangeUseCase.invoke(rechange) }

    }

    @Test
    fun updateRechange_withNullRechange_doesNotUpdateState() = runTest {
        claroRecargaViewModel.updateRechange(Rechange(id = 1, amount = 100))
        claroRecargaViewModel.updateRechange(Rechange(id = 2, amount = 200))
        assertEquals(200, claroRecargaViewModel.state.value.rechange?.amount)
    }


}