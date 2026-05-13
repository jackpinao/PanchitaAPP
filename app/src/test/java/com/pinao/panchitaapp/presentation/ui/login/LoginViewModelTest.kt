package com.pinao.panchitaapp.presentation.ui.login

import android.util.Log
import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.usecase.Auth.AuthUseCase
import com.pinao.panchitaapp.domain.usecase.products.RefreshProductsUseCase
import com.pinao.panchitaapp.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel
    private val authUseCase: AuthUseCase = mockk()
    private val refreshProductsUseCase: RefreshProductsUseCase = mockk()

    @Before
    fun setup() {
        // Mockear Log de Android
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Por defecto, usuario no logueado
        coEvery { authUseCase.isUserLoggedInUseCase() } returns false
        coEvery { refreshProductsUseCase() } returns Unit
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `init should update state to Success and sync data if user is already logged in`() = runTest {
        // Arrange
        coEvery { authUseCase.isUserLoggedInUseCase() } returns true

        // Act
        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)

        // Estabilizar las corrutinas que se lanzan en el init (como syncData)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is LoginUiState.Success)

            coVerify(exactly = 1) { refreshProductsUseCase() }
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEmailChange and onPasswordChange should update the user fields`() = runTest {
        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Estado inicial (Idle)

            viewModel.onEmailChange("test@example.com")
            val stateAfterEmail = awaitItem()
            assertEquals("test@example.com", stateAfterEmail.user.email)

            viewModel.onPasswordChange("password123")
            val stateAfterPassword = awaitItem()
            assertEquals("password123", stateAfterPassword.user.password)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login with empty fields should set Error state`() = runTest {
        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Idle con vacíos

            viewModel.login()

            val stateAfterLogin = awaitItem()
            assertTrue(stateAfterLogin is LoginUiState.Error)
            
            // Nunca llama a signInUseCase
            coVerify(exactly = 0) { authUseCase.signInUseCase(any(), any()) }
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login with valid credentials should update state to Syncing then Success`() = runTest {
        val userEmail = "test@example.com"
        val userPass = "pass123"
        val mockUser = UserModel(email = userEmail, password = userPass)
        
        coEvery { authUseCase.signInUseCase(userEmail, userPass) } returns Result.success(mockUser)

        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Estado inicial
            
            viewModel.onEmailChange(userEmail)
            awaitItem()
            
            viewModel.onPasswordChange(userPass)
            awaitItem()

            // Act
            viewModel.login()

            // Coroutine: emitimos Loading
            val loadingState = awaitItem()
            assertTrue(loadingState is LoginUiState.Loading)

            // Damos tiempo para que avance la corrutina de signIn y refresco
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            // Atrapamos el estado final (pasó de Syncing a Success rápidamente)
            val finalState = expectMostRecentItem()
            assertTrue(finalState is LoginUiState.Success)

            coVerify { authUseCase.signInUseCase(userEmail, userPass) }
            coVerify { refreshProductsUseCase() }
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout should call signOut and return to Idle state`() = runTest {
        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)
        every { authUseCase.signOutUseCase() } returns Unit
        
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Idle inicial

            viewModel.logout()

            val state = awaitItem()
            assertTrue(state is LoginUiState.Idle)
            
            verify { authUseCase.signOutUseCase() }
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init when user is logged in and syncData throws should keep Success state`() = runTest {
        coEvery { authUseCase.isUserLoggedInUseCase() } returns true
        coEvery { refreshProductsUseCase() } throws Exception("Network error during sync")

        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is LoginUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login when exception has null message should set Error with StringResource`() = runTest {
        coEvery { authUseCase.signInUseCase(any(), any()) } throws Exception()

        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // Idle inicial

            viewModel.onEmailChange("test@test.com")
            awaitItem()
            viewModel.onPasswordChange("pass123")
            awaitItem()

            viewModel.login()
            awaitItem() // Loading

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = expectMostRecentItem()
            assertTrue(errorState is LoginUiState.Error)
            assertTrue((errorState as LoginUiState.Error).message is UiText.StringResource)

            cancelAndIgnoreRemainingEvents()
        }
    }
}