package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.model.SaleDetailModel
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveDetailTicketUseCaseTest {

    private lateinit var useCase: SaveDetailTicketUseCase
    private val repository: DetailTicketRepository = mockk()

    @Before
    fun setup() {
        useCase = SaveDetailTicketUseCase(repository)
    }

    @Test
    fun `invoke should call saveTicketDetails on repository with details`() = runTest {
        val details = SaleDetailModel(saleDetailId = "d1", saleId = "s1", productId = "p1", quantity = 3.0, priceAtSale = 5.0, subtotal = 15.0)
        coEvery { repository.saveTicketDetails(any()) } returns Unit

        useCase(details)

        coVerify(exactly = 1) { 
            repository.saveTicketDetails(withArg { detail ->
                assertEquals("d1", detail.saleDetailId)
                assertEquals("s1", detail.saleId)
                assertEquals(15.0, detail.subtotal, 0.0)
            }) 
        }
    }
}