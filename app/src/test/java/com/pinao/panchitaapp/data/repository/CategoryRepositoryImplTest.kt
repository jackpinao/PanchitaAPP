package com.pinao.panchitaapp.data.repository

import android.util.Log
import app.cash.turbine.test
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.local.entity.CategoryEntity
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
    private val mockDao: CategoryDao = mockk(relaxed = true)
    private val mockFirestore: FirebaseFirestore = mockk()
    private val mockCollection: CollectionReference = mockk()
    private val mockDocument: DocumentReference = mockk()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        every { mockFirestore.collection("category") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument

        repository = CategoryRepositoryImpl(mockDao, mockFirestore)
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
    fun `refreshCategoriesFromRemote should download categories from Firestore and update Room`() = runTest {
        // Arrange
        val remoteCategories = listOf(
            CategoryModel("cat1", name = "Cat 1"),
            CategoryModel("cat2", name = "Cat 2")
        )
        val mockQuerySnapshot: QuerySnapshot = mockk()
        every { mockQuerySnapshot.toObjects(CategoryModel::class.java) } returns remoteCategories
        
        every { mockCollection.get() } returns Tasks.forResult(mockQuerySnapshot)
        
        coEvery { mockDao.insertCategory(any()) } returns 1L

        // Act
        repository.refreshCategoriesFromRemote()
        
        // Assert
        coVerify(exactly = 1) { mockCollection.get() }
        coVerify(exactly = 2) { mockDao.insertCategory(any()) }
    }
    
    @Test
    fun `refreshCategoriesFromRemote should catch Exception if Firestore fails`() = runTest {
        // Arrange
        every { mockCollection.get() } returns Tasks.forException(Exception("Network error"))

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

        val mockTask = Tasks.forResult<Void>(null)
        every { mockDocument.set(model) } returns mockTask

        coEvery { mockDao.insertCategory(any()) } returns 1L // Room insert

        repository.saveCategory(model)

        coVerify(exactly = 1) { mockDocument.set(model) }
        coVerify(exactly = 1) { 
            mockDao.insertCategory(withArg { entity -> 
                assertEquals("cat1", entity.categoryId)
                assertEquals("Carnes", entity.name)
            })
        }
    }
    
    @Test
    fun `saveCategory should catch Exception if Firestore fails`() = runTest {
        val model = CategoryModel("cat1", "store1", "Carnes", 15.0, true)

        every { mockDocument.set(model) } returns Tasks.forException(Exception("Firestore locked"))

        repository.saveCategory(model)

        coVerify(exactly = 1) { mockDocument.set(model) }
        // Si Firestore falla, actualmente no llega a ejecutar Room (por el try/catch que engloba a ambos)
        coVerify(exactly = 0) { mockDao.insertCategory(any()) }
    }

    @Test
    fun `deleteCategory should delete from Firestore and Room`() = runTest {
        val model = CategoryModel("cat_to_delete", "store1", "Borrar", 0.0, true)

        val mockTask = Tasks.forResult<Void>(null)
        every { mockDocument.delete() } returns mockTask
        
        coEvery { mockDao.deleteCategory(any()) } returns Unit

        repository.deleteCategory(model)

        coVerify(exactly = 1) { mockDocument.delete() }
        coVerify(exactly = 1) { 
            mockDao.deleteCategory(withArg { entity -> 
                assertEquals("cat_to_delete", entity.categoryId)
            }) 
        }
    }
    
    @Test
    fun `deleteCategory should catch Exception if Firestore fails`() = runTest {
        val model = CategoryModel("cat_to_delete", "store1", "Borrar", 0.0, true)

        every { mockDocument.delete() } returns Tasks.forException(Exception("Delete failed"))

        repository.deleteCategory(model)

        coVerify(exactly = 1) { mockDocument.delete() }
        coVerify(exactly = 0) { mockDao.deleteCategory(any()) }
    }
}