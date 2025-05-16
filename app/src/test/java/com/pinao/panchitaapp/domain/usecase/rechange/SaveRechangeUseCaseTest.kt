package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.RechangeModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class SaveRechangeUseCaseTest {

    private lateinit var saveRechangeUseCase: SaveRechangeUseCase
    private val repository: RechangeRepositoryImpl = mock(RechangeRepositoryImpl::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        //Configurar el dispatcher para pruebas
        kotlinx.coroutines.Dispatchers.setMain(UnconfinedTestDispatcher())
        saveRechangeUseCase = SaveRechangeUseCase(repository)
    }

    @Test
    fun `deberia guardar la recarga correctamente`() = runBlocking {
        // Given
        val rechangeModel = RechangeModel(
            date = "2021-09-01",
            amount = 1000,
            numPhone = "Test"
        )

        // Configurar el mock para no lanzar excepciones
        `when`(repository.save(rechangeModel)).thenReturn(Unit)

        // When
        saveRechangeUseCase(rechangeModel)

        // Then
        verify(repository, times(1)).save(rechangeModel)

    }

    @Test(expected = Exception::class)
    fun `deberia lanzar excepcion al guardar la recarga`() = runBlocking {
        // Given
        val rechangeModel = RechangeModel(
            date = "2021-09-01",
            amount = 1000,
            numPhone = "Test"
        )

        // Configurar el mock para lanzar una excepción
        doThrow(Exception("Error al guardar")).`when`(repository).save(rechangeModel)

        // When
        saveRechangeUseCase(rechangeModel)

        // Then
        //verify(repository, times(1)).save(rechangeModel)
    }
}