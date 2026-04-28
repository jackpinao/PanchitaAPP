package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.ProductRepository

class RefreshProductsUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke() {
        productRepository.refreshProductsFromRemote()
    }
}
