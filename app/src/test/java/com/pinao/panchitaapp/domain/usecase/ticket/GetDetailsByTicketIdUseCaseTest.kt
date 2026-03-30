package com.pinao.panchitaapp.domain.usecase.ticket

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.SaleDetailModel
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetDetailsByTicketIdUseCaseTest {

    private lateinit var useCase: GetDetailsByTicketIdUseCase
    private val repository: DetailTicketRepository = mockk()

    @Before
    fun setup() {
        useCase = GetDetailsByTicketIdUseCase(repository)
    }

    @Test
    fun `invoke should return mapped flow of SaleDetailModel from repository based on ticketId`() = runTest {
        val ticketId = "sale123"
        val expectedDetails = listOf(
            SaleDetailModel(saleDetailId = "d1", saleId = ticketId, productId = "p1", quantity = 2.0, priceAtSale = 5.0, subtotal = 10.0),
            SaleDetailModel(saleDetailId = "d2", saleId = ticketId, productId = "p2", quantity = 1.0, priceAtSale = 20.0, subtotal = 20.0)
        )
        every { repository.getDetailsByTicketId(ticketId) } returns flowOf(expectedDetails)

        val resultFlow = useCase(ticketId)

        resultFlow.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("d1", items[0].saleDetailId)
            assertEquals("d2", items[1].saleDetailId)
            assertEquals(ticketId, items[0].saleId)
            
            cancelAndIgnoreRemainingEvents()
        }
        
        verify(exactly = 1) { repository.getDetailsByTicketId(ticketId) }
    }
}