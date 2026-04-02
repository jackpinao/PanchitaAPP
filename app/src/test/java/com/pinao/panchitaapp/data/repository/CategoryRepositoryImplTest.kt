package com.pinao.panchitaapp.data.repository

import android.util.Log
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.pinao.panchitaapp.data.source.local.dao.CategoryDao
import com.pinao.panchitaapp.data.source.local.entity.CategoryEntity
import com.pinao.panchitaapp.data.source.remote.RemoteDataSource
import com.pinao.panchitaapp.domain.model.CategoryModel
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryRepositoryImplTest {

    private lateinit var repository: CategoryRepositoryImpl
    private val mockDao: CategoryDao = mockk(relaxUnitFun = true)
    private val mockRemoteDataSource: RemoteDataSource = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        repository = CategoryRepositoryImpl(
            categoryDao = mockDao,
            remoteDataSource = mockRemoteDataSource
        )
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `getAllCategoriesFromDataBase should emit mapped categories from Dao`() = runTest {
        val entities = listOf(
            CategoryEntity("cat1", "store1", "Lácteos", 10.0, 1),
            CategoryEntity("cat2", "store1", "Dulces", 20.0, 0)
        )
        every { mockDao.getAllCategories() } returns flowOf(entities)

        repository.getAllCategoriesFromDataBase().test {
            val models = awaitItem()
            assertEquals(2, models.size)
            assertEquals("Lácteos", models[0].name)
            assertEquals(true, models[0].isSynced)
            assertEquals("Dulces", models[1].name)
            assertEquals(false, models[1].isSynced)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshCategoriesFromRemote should download categories from Firestore and update Room`() =
        runTest {
            // Arrange
            val remoteCategories = listOf(
                CategoryModel("cat1", name = "Cat 1"),
                CategoryModel("cat2", name = "Cat 2")
            )
            coEvery { mockRemoteDataSource.categoryRemoteDataSource.getCategories() } returns remoteCategories
            coEvery { mockDao.insertCategory(any()) } returns 1L

            // Act
            repository.refreshCategoriesFromRemote()

            // Assert
            coVerify(exactly = 1) { mockRemoteDataSource.categoryRemoteDataSource.getCategories() }
            
            // Verificamos los insert individualmente usando match en lugar de withArg para evitar AssertionErrors internos de MockK
            coVerify(exactly = 1) { mockDao.insertCategory(match { it.categoryId == "cat1" }) }
            coVerify(exactly = 1) { mockDao.insertCategory(match { it.categoryId == "cat2" }) }
        }

    @Test
    fun `refreshCategoriesFromRemote should catch Exception if Firestore fails`() = runTest {
        // Arrange
        coEvery { mockRemoteDataSource.categoryRemoteDataSource.getCategories() } throws Exception("Firestore locked")

        // Act
        repository.refreshCategoriesFromRemote()

        // Assert
        coVerify(exactly = 0) { mockDao.insertCategory(any()) }
    }

    @Test
    fun `findCodeCategory should return mapped category from Dao`() = runTest {
        val entity = CategoryEntity("cat_code", "store1", "Bebidas", 5.0, 1)
        every { mockDao.findCodeCategory("cat_code") } returns flowOf(entity)

        repository.findCodeCategory("cat_code").test {
            val model = awaitItem()
            assertEquals("cat_code", model?.categoryId)
            assertEquals("Bebidas", model?.name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `findCodeCategory should return null if not found`() = runTest {
        every { mockDao.findCodeCategory("unknown") } returns flowOf(null)

        repository.findCodeCategory("unknown").test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveCategory should save in Firestore and Room`() = runTest {
        val model = CategoryModel("cat1", "store1", "Carnes", 15.0, true)

        coEvery { mockRemoteDataSource.categoryRemoteDataSource.saveCategory(any()) } returns true
        coEvery { mockDao.insertCategory(any()) } returns 1L

        repository.saveCategory(model)

        coVerify(exactly = 1) { mockRemoteDataSource.categoryRemoteDataSource.saveCategory(any()) }
        coVerify(exactly = 1) { mockDao.insertCategory(match { it.categoryId == "cat1" && it.name == "Carnes" && it.isSynced == 1 }) }
    }

    @Test
    fun `saveCategory should catch Exception if Firestore fails`() = runTest {
        val model = CategoryModel("cat1", "store1", "Carnes", 15.0, true)

        coEvery { mockRemoteDataSource.categoryRemoteDataSource.saveCategory(any()) } returns false
        coEvery { mockDao.insertCategory(any()) } returns 1L

        repository.saveCategory(model)

        coVerify(exactly = 1) { mockRemoteDataSource.categoryRemoteDataSource.saveCategory(any()) }
        coVerify(exactly = 1) { mockDao.insertCategory(match { it.categoryId == "cat1" && it.isSynced == 0 }) }
    }

    @Test
    fun `deleteCategory should delete from Firestore and Room`() = runTest {
        val model = CategoryModel("cat_to_delete", "store1", "Borrar", 0.0, true)

        coEvery { mockRemoteDataSource.categoryRemoteDataSource.deleteCategory(any()) } returns true
        coEvery { mockDao.deleteCategory(any()) } returns Unit

        repository.deleteCategory(model)

        coVerify(exactly = 1) { mockRemoteDataSource.categoryRemoteDataSource.deleteCategory(any()) }
        coVerify(exactly = 1) { mockDao.deleteCategory(match { it.categoryId == "cat_to_delete" }) }
    }

    @Test
    fun `deleteCategory should catch Exception if Firestore fails`() = runTest {
        val model = CategoryModel("cat_to_delete", "store1", "Borrar", 0.0, true)

        coEvery { mockRemoteDataSource.categoryRemoteDataSource.deleteCategory(any()) } returns false

        repository.deleteCategory(model)

        coVerify(exactly = 1) { mockRemoteDataSource.categoryRemoteDataSource.deleteCategory(any()) }
        coVerify(exactly = 0) { mockDao.deleteCategory(any()) }
    }
}