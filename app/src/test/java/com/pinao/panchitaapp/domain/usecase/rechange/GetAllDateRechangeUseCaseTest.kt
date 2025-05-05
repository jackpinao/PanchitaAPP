package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.RechangeModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GetAllDateRechangeUseCaseTest {

    private lateinit var getAllDateRechangeUseCase: GetAllDateRechangeUseCase
    private val repository: RechangeRepositoryImpl = mock(RechangeRepositoryImpl::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(UnconfinedTestDispatcher())
        getAllDateRechangeUseCase = GetAllDateRechangeUseCase(repository)
    }

    @Test
    fun returnsListOfRechangesFromRepository() {
        runBlocking {
            // Given
            val rechangeList = listOf(
                RechangeModel(date = "2023-10-01", amount = 100, numPhone = "123456789"),
                RechangeModel(date = "2023-10-02", amount = 200, numPhone = "987654321")
            )
            `when`(repository.listAllDateRechangeFromDataBase()).thenReturn(flowOf(rechangeList))
            //`when`(repository.listAllDateRechangeFromDataBase()).thenReturn(rechangeList.asFlow())

            // When
            //val result = getAllDateRechangeUseCase().toList()
            val result = getAllDateRechangeUseCase()

            // Then
            //assert(result == rechangeList)
            //assertEquals(rechangeList, result)
            result.collect { rechanges ->
                assertEquals(rechangeList, rechanges)
            }
            verify(repository, times(1)).listAllDateRechangeFromDataBase()
        }
    }

    @Test
    fun returnsEmptyListWhenNoRechangesExist() {
        runBlocking {
            // Given
            `when`(repository.listAllDateRechangeFromDataBase()).thenReturn(flowOf(emptyList()))

            // When
            //val result = getAllDateRechangeUseCase().toList()
            val result = getAllDateRechangeUseCase()

            // Then
            //assert(result.isEmpty())
            result.collect { rechanges ->
                //assertEquals(rechanges, emptyList<RechangeModel>())
                assertTrue(rechanges.isEmpty())
            }
            verify(repository, times(1)).listAllDateRechangeFromDataBase()
        }
    }
}