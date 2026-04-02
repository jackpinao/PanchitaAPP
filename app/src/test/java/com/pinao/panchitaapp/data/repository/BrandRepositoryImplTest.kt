package com.pinao.panchitaapp.data.repository

import android.util.Log
import app.cash.turbine.test
import com.pinao.panchitaapp.data.source.local.dao.BrandDao
import com.pinao.panchitaapp.data.source.local.entity.BrandEntity
import com.pinao.panchitaapp.domain.model.BrandModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BrandRepositoryImplTest {

    private lateinit var repository: BrandRepositoryImpl
    private val mockBrandDao: BrandDao = mockk()

    @Before
    fun setup() {
        // Mockeamos la clase Log de Android para evitar que crashee en tests unitarios locales
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0

        repository = BrandRepositoryImpl(mockBrandDao)
    }

    @After
    fun tearDown() {
        // Limpiamos los mocks estáticos
        unmockkStatic(Log::class)
    }

    @Test
    fun `getAllBrands should emit list of BrandModel when DAO emits BrandEntity list`() = runTest {
        // Given
        val mockEntities = listOf(
            BrandEntity("brand1", "store1", "Coca-Cola", 1),
            BrandEntity("brand2", "store1", "Inca Kola", 0)
        )
        
        // Cuando el DAO llame a getAll(), le decimos que emita nuestro Flow falso
        every { mockBrandDao.getAll() } returns flowOf(mockEntities)

        // When
        val resultFlow = repository.getAllBrands()

        // Then
        resultFlow.test {
            val domainList = awaitItem()
            
            assertEquals(2, domainList.size)
            
            // Verificamos que se haya mapeado correctamente a nuestro modelo de Dominio
            assertEquals("brand1", domainList[0].brandId)
            assertEquals("Coca-Cola", domainList[0].name)
            
            assertEquals("brand2", domainList[1].brandId)
            assertEquals("Inca Kola", domainList[1].name)

            cancelAndIgnoreRemainingEvents()
        }
        
        // Verificamos que efectivamente se llamó al DAO
        coVerify(exactly = 1) { mockBrandDao.getAll() }
    }

    @Test
    fun `getBrandById should emit correct BrandModel or null`() = runTest {
        // Given
        val entity = BrandEntity("brand99", "store1", "Oreo", 1)
        val testId = 99
        
        every { mockBrandDao.getBrandById(testId) } returns flowOf(entity)

        // When
        val resultFlow = repository.getBrandById(testId)

        // Then
        resultFlow.test {
            val domainModel = awaitItem()
            
            assertNotNull(domainModel)
            assertEquals("brand99", domainModel?.brandId)
            assertEquals("Oreo", domainModel?.name)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getBrandById should emit null when DAO returns null`() = runTest {
        // Given
        val testId = 100
        every { mockBrandDao.getBrandById(testId) } returns flowOf(null)

        // When
        val resultFlow = repository.getBrandById(testId)

        // Then
        resultFlow.test {
            val domainModel = awaitItem()
            assertNull(domainModel)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveBrand should call upsertAll on DAO`() = runTest {
        // Given
        val brandToSave = BrandModel(
            brandId = "b1",
            name = "Gloria",
            isSynced = false
        )
        
        // Ignoramos el resultado de la suspend function (Unit)
        coEvery { mockBrandDao.upsertAll(any()) } returns Unit

        // When
        repository.saveBrand(brandToSave)

        // Then
        coVerify(exactly = 1) { 
            // Verificamos que el Repositorio pasó la entidad mapeada hacia el DAO
            mockBrandDao.upsertAll(
                withArg { entity ->
                    assertEquals("b1", entity.brandId)
                    assertEquals("Gloria", entity.name)
                    assertEquals(0, entity.isSynced)
                }
            )
        }
    }

    @Test
    fun `deleteBrand should call deleteAll on DAO`() = runTest {
        // Given
        val brandToDelete = BrandModel(
            brandId = "b2",
            name = "San Luis",
            isSynced = true
        )
        
        coEvery { mockBrandDao.deleteAll(any()) } returns Unit

        // When
        repository.deleteBrand(brandToDelete)

        // Then
        coVerify(exactly = 1) { 
            mockBrandDao.deleteAll(
                withArg { entity ->
                    assertEquals("b2", entity.brandId)
                    assertEquals("San Luis", entity.name)
                    assertEquals(1, entity.isSynced)
                }
            )
        }
    }
}
