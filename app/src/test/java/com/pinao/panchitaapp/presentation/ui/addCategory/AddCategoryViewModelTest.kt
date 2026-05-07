package com.pinao.panchitaapp.presentation.ui.addCategory

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.usecase.category.CheckCategoryNameUseCase
import com.pinao.panchitaapp.domain.usecase.category.SaveCategoryUseCase
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddCategoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AddCategoryViewModel
    private val saveCategoryUseCase: SaveCategoryUseCase = mockk()
    private val checkCategoryNameUseCase: CheckCategoryNameUseCase = mockk()
    private val sessionManager: SessionManager = mockk()

    @Before
    fun setup() {
        coEvery { saveCategoryUseCase(any()) } returns Unit
        coEvery { checkCategoryNameUseCase(any()) } returns false
        every { sessionManager.getStoreId() } returns "test-store-id"
    }

    @Test
    fun `onNameChange should update the state`() = runTest {
        viewModel = AddCategoryViewModel(saveCategoryUseCase, checkCategoryNameUseCase, sessionManager)

        viewModel.uiState.test {
            awaitItem() // Estado inicial (Idle)

            viewModel.onNameChange("Lácteos")
            val stateAfterName = awaitItem()
            assertEquals("Lácteos", stateAfterName.category.name)
        }
    }

    @Test
    fun `saveCategory with blank name should return Error state`() = runTest {
        viewModel = AddCategoryViewModel(saveCategoryUseCase, checkCategoryNameUseCase, sessionManager)

        viewModel.uiState.test {
            awaitItem() // Idle inicial

            viewModel.saveCategory()

            val errorState = awaitItem()
            assertTrue(errorState is AddCategoryUiState.Error)
            
            coVerify(exactly = 0) { saveCategoryUseCase(any()) }
            coVerify(exactly = 0) { checkCategoryNameUseCase(any()) }
        }
    }

    @Test
    fun `saveCategory with existing name should return Error state`() = runTest {
        val categoryName = "Bebidas"
        coEvery { checkCategoryNameUseCase(categoryName) } returns true

        viewModel = AddCategoryViewModel(saveCategoryUseCase, checkCategoryNameUseCase, sessionManager)

        viewModel.uiState.test {
            awaitItem() // Inicial
            
            viewModel.onNameChange(categoryName)
            awaitItem() // Actualiza nombre

            viewModel.saveCategory()

            val loadingState = awaitItem()
            assertTrue(loadingState is AddCategoryUiState.Loading)

            val errorState = awaitItem()
            assertTrue(errorState is AddCategoryUiState.Error)

            coVerify { checkCategoryNameUseCase(categoryName) }
            coVerify(exactly = 0) { saveCategoryUseCase(any()) }
        }
    }

    @Test
    fun `saveCategory with valid inputs should return Success state`() = runTest {
        val categoryName = "Nueva Categoría"

        coEvery { checkCategoryNameUseCase(categoryName) } returns false
        coEvery { saveCategoryUseCase(any()) } returns Unit

        viewModel = AddCategoryViewModel(saveCategoryUseCase, checkCategoryNameUseCase, sessionManager)

        viewModel.uiState.test {
            awaitItem() // Inicial

            viewModel.onNameChange(categoryName)
            awaitItem() // Actualiza name

            viewModel.saveCategory()

            val loadingState = awaitItem()
            assertTrue(loadingState is AddCategoryUiState.Loading)

            val successState = awaitItem()
            assertTrue(successState is AddCategoryUiState.Success)
            assertEquals(categoryName, successState.category.name)

            coVerify { checkCategoryNameUseCase(categoryName) }
            coVerify { saveCategoryUseCase(any()) }
        }
    }
}
