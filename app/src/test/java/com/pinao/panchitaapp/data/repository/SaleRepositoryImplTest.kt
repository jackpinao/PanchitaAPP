package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.pinao.panchitaapp.data.local.dao.SaleDao
import com.pinao.panchitaapp.data.local.dao.SaleDetailDao
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaleRepositoryImplTest {

    private lateinit var repository: SaleRepositoryImpl
    private val mockSaleDao: SaleDao = mockk(relaxed = true)
    private val mockSaleDetailDao: SaleDetailDao = mockk(relaxed = true)
    private val mockFirestore: FirebaseFirestore = mockk(relaxed = true)
    
    private val mockBatch: WriteBatch = mockk(relaxed = true)
    private val mockCollection: CollectionReference = mockk(relaxed = true)
    private val mockDocument: DocumentReference = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        every { mockFirestore.collection(any()) } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
        every { mockDocument.collection(any()) } returns mockCollection
        
        every { mockFirestore.batch() } returns mockBatch
        every { mockBatch.commit() } returns Tasks.forResult(null)

        repository = SaleRepositoryImpl(mockSaleDao, mockSaleDetailDao, mockFirestore)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `saveFullSale should save in Room and Firestore Batch successfully`() = runTest {
        // Given
        val sale = SaleModel(
            saleId = "sale1",
            storeId = "store1",
            userId = "user1",
            clientId = "client1",
            saleDate = "2023-10-10",
            totalAmount = 50.0,
            paymentType = "CASH",
            isSynced = false
        )
        val products = listOf(
            ProductModel(
                productId = "prod1",
                name = "P1",
                barcode = "111",
                priceSell = 10.0,
                stockQuantity = 2.0
            ),
            ProductModel(
                productId = "prod2",
                name = "P2",
                barcode = "222",
                priceSell = 15.0,
                stockQuantity = 2.0
            )
        )

        coEvery { mockSaleDao.saveFullSale(any(), any()) } returns Unit

        // When
        repository.saveFullSale(sale, products)

        // Then
        // Verify Room transaction called
        coVerify(exactly = 1) { mockSaleDao.saveFullSale(any(), any()) }
        
        // El ticket original (1) + las entidades de detalle de venta en "detail_ticket" (2) + los productos en la subcoleccion "items" del ticket (2)
        // Total de llamadas a set = 5
        coVerify(exactly = 5) { mockBatch.set(any(), any<Any>()) }
        
        // La rebaja del stock maestro en Firestore por los 2 productos
        // Total de llamadas a update = 2
        coVerify(exactly = 2) { mockBatch.update(any(), "stock", any<Any>()) }
        
        // Todo consolidado en un solo commit a Firebase
        coVerify(exactly = 1) { mockBatch.commit() }
    }
}