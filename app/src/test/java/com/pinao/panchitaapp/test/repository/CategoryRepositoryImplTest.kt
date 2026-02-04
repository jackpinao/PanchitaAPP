package com.pinao.panchitaapp.test.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.repository.CategoryRepositoryImpl
import com.pinao.panchitaapp.domain.model.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryRepositoryImplTest {

    private lateinit var categoryDao: CategoryDao
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: CategoryRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        categoryDao = mock(CategoryDao::class.java)
        firestore = mock(FirebaseFirestore::class.java)
        repository = CategoryRepositoryImpl(categoryDao, firestore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshCategoriesFromRemote fetches from Firestore and saves to Room`() = runTest {
        val collectionReference = mock(CollectionReference::class.java)
        val querySnapshot = mock(QuerySnapshot::class.java)
        val categories = listOf(CategoryModel(name = "Lácteos"))

        `when`(firestore.collection("categories")).thenReturn(collectionReference)
        `when`(collectionReference.get()).thenReturn(Tasks.forResult(querySnapshot))
        `when`(querySnapshot.toObjects(CategoryModel::class.java)).thenReturn(categories)

        repository.refreshCategoriesFromRemote()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verificamos que se llamó al DAO para insertar la categoría descargada
        verify(categoryDao).insertCategory(any())
    }

    @Test
    fun `saveCategory saves to Firestore and then to Room`() = runTest {
        val category = CategoryModel(name = "Frutas")
        val collectionReference = mock(CollectionReference::class.java)
        val documentReference = mock(DocumentReference::class.java)

        `when`(firestore.collection("categories")).thenReturn(collectionReference)
        `when`(collectionReference.document(category.id)).thenReturn(documentReference)
        `when`(documentReference.set(category)).thenReturn(Tasks.forResult(null))

        repository.saveCategory(category)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verifica guardado local
        verify(categoryDao).insertCategory(any())
    }
}
