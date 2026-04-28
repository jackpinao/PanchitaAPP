package com.pinao.panchitaapp.domain.usecase.ticket

import com.google.common.truth.Truth.assertThat
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import com.pinao.panchitaapp.domain.repository.SaleRepository
import io.mockk.mockk
import org.junit.Test

class DetailTicketUseCasesTest {

    private val detailRepository: DetailTicketRepository = mockk()
    private val saleRepository: SaleRepository = mockk()

    @Test
    fun `DetailTicketUseCases equals returns true for same property instances`() {
        val save = SaveDetailTicketUseCase(detailRepository)
        val getDetails = GetDetailsByTicketIdUseCase(detailRepository)

        val bundle1 = DetailTicketUseCases(save, getDetails)
        val bundle2 = DetailTicketUseCases(save, getDetails)

        assertThat(bundle1).isEqualTo(bundle2)
    }

    @Test
    fun `DetailTicketUseCases equals returns false for different property instances`() {
        val save1 = SaveDetailTicketUseCase(detailRepository)
        val save2 = SaveDetailTicketUseCase(mockk())
        val getDetails = GetDetailsByTicketIdUseCase(detailRepository)

        val bundle1 = DetailTicketUseCases(save1, getDetails)
        val bundle2 = DetailTicketUseCases(save2, getDetails)

        assertThat(bundle1).isNotEqualTo(bundle2)
    }

    @Test
    fun `DetailTicketUseCases equals returns false when compared to null`() {
        val bundle = DetailTicketUseCases(
            SaveDetailTicketUseCase(detailRepository),
            GetDetailsByTicketIdUseCase(detailRepository)
        )

        assertThat(bundle).isNotEqualTo(null)
    }

    @Test
    fun `DetailTicketUseCases hashCode is consistent for equal instances`() {
        val save = SaveDetailTicketUseCase(detailRepository)
        val getDetails = GetDetailsByTicketIdUseCase(detailRepository)

        val bundle1 = DetailTicketUseCases(save, getDetails)
        val bundle2 = DetailTicketUseCases(save, getDetails)

        assertThat(bundle1.hashCode()).isEqualTo(bundle2.hashCode())
    }
}
