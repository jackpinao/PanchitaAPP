package com.pinao.panchitaapp.presentation.ui.home

import com.pinao.panchitaapp.test.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel()
    }

    @Test
    fun `viewModel should be instantiated correctly`() {
        assertNotNull(viewModel)
    }
}
