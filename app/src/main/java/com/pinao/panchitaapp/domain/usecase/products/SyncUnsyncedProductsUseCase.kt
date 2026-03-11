package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.ProductRepository

class SyncUnsyncedProductsUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke() = repository.syncUnsyncedProducts()
}
