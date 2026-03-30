package com.pinao.panchitaapp.data.repository

import android.util.Log
import app.cash.turbine.test
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.local.dao.BrandDao
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import com.pinao.panchitaapp.domain.model.ProductModel
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
class ProductsRepositoryImplTest {

    private lateinit var repository: ProductsRepositoryImpl
    private val mockProductDao: ProductDao = mockk(relaxed = true)
    private val mockCategoryDao: CategoryDao = mockk(relaxed = true)
    private val mockBrandDao: BrandDao = mockk(relaxed = true)
    private val mockFirestore: FirebaseFirestore = mockk(relaxed = true)

    private val mockCollection: CollectionReference = mockk(relaxed = true)
    private val mockDocument: DocumentReference = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        every { mockFirestore.collection(any()) } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument

        repository = ProductsRepositoryImpl(mockProductDao, mockCategoryDao, mockBrandDao, mockFirestore)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `getAllProductsFromDataBase should return mapped flow of products`() = runTest {
        val entities = listOf(
            ProductsEntity(
                productId = "p1", storeId = "s1", categoryId = "c1", brandId = "b1", detailTicketEntityId = "d1",
                name = "Prod1", description = "Desc1", priceBuy = 10.0, priceSell = 15.0, priceExcludingIGV = 12.0,
                stockQuantity = 100.0, stockMin = 5.0, barcode = "111", image = "img", lastUpdated = "date", isSynced = 1
            )
        )
        every { mockProductDao.getAllProducts() } returns flowOf(entities)

        repository.getAllProductsFromDataBase().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("p1", items[0].productId)
            assertEquals("Prod1", items[0].name)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `findCodeProduct should return mapped product if found`() = runTest {
        val entity = ProductsEntity(
            productId = "p2", storeId = "s1", categoryId = "c1", brandId = "b1", detailTicketEntityId = "d1",
            name = "Prod2", description = "Desc2", priceBuy = 20.0, priceSell = 25.0, priceExcludingIGV = 22.0,
            stockQuantity = 50.0, stockMin = 5.0, barcode = "222", image = "img", lastUpdated = "date", isSynced = 0
        )
        every { mockProductDao.findCodeProduct("222") } returns flowOf(entity)

        repository.findCodeProduct("222").test {
            val model = awaitItem()
            assertEquals("p2", model?.productId)
            assertEquals("Prod2", model?.name)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `findCodeProduct should return null if not found`() = runTest {
        every { mockProductDao.findCodeProduct("999") } returns flowOf(null)

        repository.findCodeProduct("999").test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProduct should save to Firestore and Room as synced`() = runTest {
        val model = ProductModel(
            productId = "p3", storeId = "s1", categoryId = "c1", brandId = "b1", detailTicketEntityId = "d1",
            name = "Prod3", priceSell = 30.0, priceBuy = 25.0, priceExcludingIGV = 28.0, description = "Desc3",
            image = "img", stockQuantity = 10.0, barcode = "333", lastUpdated = "date", stockMin = 5.0, isSynced = false
        )
        
        // Simular éxito en Firebase
        every { mockDocument.set(model) } returns Tasks.forResult(null)

        repository.saveProduct(model)

        coVerify(exactly = 1) { mockDocument.set(model) }
        coVerify(exactly = 1) { 
            mockProductDao.insertProduct(withArg { entity -> 
                assertEquals("p3", entity.productId)
                assertEquals(1, entity.isSynced) // Debería cambiar a true (1) al guardarse exitosamente
            })
        }
    }

    @Test
    fun `saveProduct should save to Room as unsynced if Firestore fails`() = runTest {
        val model = ProductModel(
            productId = "p4", storeId = "s1", categoryId = "c1", brandId = "b1", detailTicketEntityId = "d1",
            name = "Prod4", priceSell = 40.0, priceBuy = 35.0, priceExcludingIGV = 38.0, description = "Desc4",
            image = "img", stockQuantity = 10.0, barcode = "444", lastUpdated = "date", stockMin = 5.0, isSynced = true
        )
        
        // Simular fallo en Firebase
        every { mockDocument.set(model) } returns Tasks.forException(Exception("Network error"))

        repository.saveProduct(model)

        coVerify(exactly = 1) { mockDocument.set(model) }
        coVerify(exactly = 1) { 
            mockProductDao.insertProduct(withArg { entity -> 
                assertEquals("p4", entity.productId)
                assertEquals(0, entity.isSynced) // Debería cambiar a false (0) al fallar Firebase
            })
        }
    }

    @Test
    fun `deleteProduct should delete from Firestore and Room`() = runTest {
        val model = ProductModel(productId = "p5", name = "Prod5")
        every { mockDocument.delete() } returns Tasks.forResult(null)

        repository.deleteProduct(model)

        coVerify(exactly = 1) { mockDocument.delete() }
        coVerify(exactly = 1) { mockProductDao.deleteProduct(any()) }
    }
}