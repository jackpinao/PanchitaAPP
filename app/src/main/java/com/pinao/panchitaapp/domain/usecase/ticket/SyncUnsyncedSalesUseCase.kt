package com.pinao.panchitaapp.domain.usecase.ticket

import com.pinao.panchitaapp.domain.repository.SaleRepository

class SyncUnsyncedSalesUseCase(
    private val repository: SaleRepository
) {
    suspend operator fun invoke() = repository.syncUnsyncedSales()
}
