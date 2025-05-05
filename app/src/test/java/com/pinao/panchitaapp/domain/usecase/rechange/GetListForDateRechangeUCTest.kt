package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.RechangeModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times

class GetListForDateRechangeUCTest {

    private lateinit var getlist: GetListForDateRechangeUC
    private val repository: RechangeRepositoryImpl = mock(RechangeRepositoryImpl::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(UnconfinedTestDispatcher())
        getlist = GetListForDateRechangeUC(repository)
    }

    @Test
    fun returnsListOfRechangesFromRepository() {
        runBlocking {
            // Given
            val fecha = "2023-10-01"
            val rechangeList = listOf(
                RechangeModel(date = "2023-10-01", amount = 100, numPhone = "123456789")
            )
            `when`(repository.listForDate(fecha)).thenReturn(flowOf(rechangeList))

            // When
            val result = getlist("2023-10-01")

            // Then
            result.collect { rechanges ->
                assertEquals(rechangeList, rechanges)
            }
            //assertEquals(rechangeList, result)
            verify(repository, times(1)).listForDate(fecha)
        }
    }

    @Test
    fun returnsEmptyListWhenNoRechangesExist() {
        runBlocking {
            // Given
            val fecha = "2023-10-01"
            `when`(repository.listForDate(fecha)).thenReturn(flowOf(emptyList()))

            // When
            val result = getlist("2023-10-01")

            // Then
            result.collect { rechanges ->
                assertTrue(rechanges.isEmpty())
            }
            verify(repository, times(1)).listForDate(fecha)
        }
    }
}