package com.pinao.panchitaapp.data.repository

import android.util.Log
import app.cash.turbine.test
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.dao.SaleDetailDao
import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.domain.model.SaleDetailModel
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailTicketRepositoryImplTest {

    private lateinit var repository: DetailTicketRepositoryImpl
    private val mockDao: SaleDetailDao = mockk(relaxed = true)
    private val mockFirestore: FirebaseFirestore = mockk(relaxed = true)
    private val mockCollection: CollectionReference = mockk(relaxed = true)
    private val mockDocument: DocumentReference = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        every { mockFirestore.collection("detail_ticket") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument

        repository = DetailTicketRepositoryImpl(mockDao, mockFirestore)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `saveTicketDetails should save to Firestore and Room`() = runTest {
        val model = SaleDetailModel(
            saleDetailId = "detail1",
            saleId = "sale1",
            productId = "prod1",
            quantity = 2.0,
            priceAtSale = 10.0,
            subtotal = 20.0
        )

        every { mockDocument.set(model) } returns Tasks.forResult(null)

        repository.saveTicketDetails(model)

        coVerify(exactly = 1) { mockDocument.set(model) }
        coVerify(exactly = 1) { 
            mockDao.insertDetails(withArg { entity -> 
                assertEquals("detail1", entity.saleDetailId)
                assertEquals("sale1", entity.saleId)
            }) 
        }
    }

    @Test
    fun `getDetailsByTicketId should return mapped list from Dao`() = runTest {
        val entities = listOf(
            SaleDetailEntity(
                saleDetailId = "detail2",
                saleId = "sale_id_test",
                productId = "prod2",
                quantity = 5.0,
                priceAtSale = 1.0,
                subtotal = 5.0
            )
        )
        every { mockDao.getDetailsByTicketId("sale_id_test") } returns flowOf(entities)

        repository.getDetailsByTicketId("sale_id_test").test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("detail2", items[0].saleDetailId)
            assertEquals("prod2", items[0].productId)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        coVerify(exactly = 1) { mockDao.getDetailsByTicketId("sale_id_test") }
    }
}