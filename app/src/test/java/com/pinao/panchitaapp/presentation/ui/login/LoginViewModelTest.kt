package com.pinao.panchitaapp.presentation.ui.login

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.usecase.Auth.AuthUseCase
import com.pinao.panchitaapp.domain.usecase.products.RefreshProductsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val authUseCase: AuthUseCase = mockk()
    private val refreshProductsUseCase: RefreshProductsUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Por defecto, usuario no logueado
        every { authUseCase.isUserLoggedInUseCase() } returns false
        coEvery { refreshProductsUseCase() } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should update state to Success and sync data if user is already logged in`() = runTest {
        // Arrange
        every { authUseCase.isUserLoggedInUseCase() } returns true

        // Act
        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is LoginUiState.Success)

            // Damos tiempo para que viewModelScope.launch ejecute el syncData()
            testScheduler.advanceUntilIdle()

            coVerify(exactly = 1) { refreshProductsUseCase() }
        }
    }

    @Test
    fun `onEmailChange and onPasswordChange should update the user fields`() = runTest {
        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)

        viewModel.uiState.test {
            awaitItem() // Estado inicial (Idle)

            viewModel.onEmailChange("test@example.com")
            val stateAfterEmail = awaitItem()
            assertEquals("test@example.com", stateAfterEmail.user.email)

            viewModel.onPasswordChange("password123")
            val stateAfterPassword = awaitItem()
            assertEquals("password123", stateAfterPassword.user.password)
        }
    }

    @Test
    fun `login with empty fields should set Error state`() = runTest {
        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)

        viewModel.uiState.test {
            awaitItem() // Idle con vacíos

            viewModel.login()

            val stateAfterLogin = awaitItem()
            assertTrue(stateAfterLogin is LoginUiState.Error)
            
            // Nunca llama a signInUseCase
            coVerify(exactly = 0) { authUseCase.signInUseCase(any(), any()) }
        }
    }

    @Test
    fun `login with valid credentials should update state to Syncing then Success`() = runTest {
        val userEmail = "test@example.com"
        val userPass = "pass123"
        val mockUser = UserModel(email = userEmail, password = userPass)
        
        coEvery { authUseCase.signInUseCase(userEmail, userPass) } returns Result.success(mockUser)

        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)

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

            // Damos tiempo para que avance la corrutina
            testScheduler.advanceUntilIdle()

            // Pasamos por Syncing
            val syncingState = awaitItem()
            assertTrue(syncingState is LoginUiState.Syncing)
            
            // Finalmente Success
            val successState = awaitItem()
            assertTrue(successState is LoginUiState.Success)

            coVerify { authUseCase.signInUseCase(userEmail, userPass) }
            coVerify { refreshProductsUseCase() }
        }
    }

    @Test
    fun `logout should call signOut and return to Idle state`() = runTest {
        viewModel = LoginViewModel(authUseCase, refreshProductsUseCase)
        every { authUseCase.signOutUseCase() } returns Unit

        viewModel.uiState.test {
            awaitItem() // Idle inicial

            viewModel.logout()

            val state = awaitItem()
            assertTrue(state is LoginUiState.Idle)
            
            verify { authUseCase.signOutUseCase() }
        }
    }
}
