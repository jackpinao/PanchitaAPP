package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.pinao.panchitaapp.data.network.pos.SaleApiClient
import com.pinao.panchitaapp.data.source.local.dao.SaleDao
import com.pinao.panchitaapp.data.source.local.dao.SaleDetailDao
import com.pinao.panchitaapp.data.source.remote.dto.SaleResponseDto
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
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SaleRepositoryImplTest {
    private lateinit var repository: SaleRepositoryImpl
    private val mockSaleDao: SaleDao = mockk(relaxed = true)
    private val mockSaleDetailDao: SaleDetailDao = mockk(relaxed = true)
    private val mockSaleApiClient: SaleApiClient = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        repository = SaleRepositoryImpl(
            saleDao = mockSaleDao,
            saleDetailDao = mockSaleDetailDao,
            saleApiClient = mockSaleApiClient
        )
    }


    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `saveFullSale should save pending locally and mark it synced after API success`() = runTest {
        // Given
        val sale = SaleModel(
            saleId = "sale1",
            storeId = "store1",
            userId = "user1",
            clientId = "client1",
            saleDate = "2023-10-10",
            totalAmount = 50.0,
            paymentType = "CASH",
            isSynced = true
        )
        val products = listOf(
            ProductModel(
                productId = "prod1",
                name = "P1",
                barcode = "111",
                priceSell = 10.0,
                stockQuantity = 2.0
            )
        )

        coEvery { mockSaleDao.saveFullSale(any(), any()) } returns Unit
        coEvery {
            mockSaleApiClient.processSale(any(), any(), any())
        } returns Response.success(SaleResponseDto(message = "ok"))

        // When
        repository.saveFullSale(sale, products)

        // Then
        coVerify(exactly = 1) {
            mockSaleDao.saveFullSale(
                match { it.saleId == "sale1" && it.isSynced == 0 },
                any()
            )
        }
        coVerify(exactly = 1) { mockSaleDao.updateSyncStatus("sale1", 1) }
        coVerify(exactly = 1) {
            mockSaleApiClient.processSale(
                match { it == "store1" },
                match { it == "user1" },
                match {
                    it.offlineId == "sale1" &&
                        it.items.single().productId == "prod1" &&
                        it.items.single().quantity == 2.0
                }
            )
        }
    }
}
