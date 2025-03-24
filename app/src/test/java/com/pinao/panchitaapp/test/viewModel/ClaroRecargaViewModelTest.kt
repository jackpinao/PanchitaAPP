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
import org.junit.Assert.assertNotEquals
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

//    @Test
//    fun updateRechange2_updatesStateWithNewRechange() = runTest {
//        val numPhone = "1234567890"
//        val amount = 100
//        val date = "2023-10-10"
//        claroRecargaViewModel.updateRechange2(numPhone, amount, date)
//        assertEquals(amount, claroRecargaViewModel.state.value.rechange?.amount)
//        assertEquals(numPhone, claroRecargaViewModel.state.value.rechange?.numPhone)
//        assertEquals(date, claroRecargaViewModel.state.value.rechange?.date)
//    }
//
//    @Test
//    fun updateRechange2_callsSaveRechangeUseCase() = runTest {
//        val numPhone = "1234567890"
//        val amount = 100
//        val date = "2023-10-10"
//        coEvery { saveRechangeUseCase(any()) } returns Unit
//        claroRecargaViewModel.updateRechange2(numPhone, amount, date)
//        coVerify { saveRechangeUseCase.invoke(Rechange(numPhone = numPhone, amount = amount, date = date)) }
//    }
//
//    @Test
//    fun updateRechange2_withInvalidAmount_doesNotUpdateState() = runTest {
//        val numPhone = "1234567890"
//        val amount = -100
//        val date = "2023-10-10"
//        claroRecargaViewModel.updateRechange2(numPhone, amount, date)
//        assertNotEquals(amount, claroRecargaViewModel.state.value.rechange?.amount)
//    }
//
//    @Test
//    fun updateRechange2_withEmptyPhone_doesNotUpdateState() = runTest {
//        val numPhone = ""
//        val amount = 100
//        val date = "2023-10-10"
//        claroRecargaViewModel.updateRechange2(numPhone, amount, date)
//        assertNotEquals(numPhone, claroRecargaViewModel.state.value.rechange?.numPhone)
//    }

}