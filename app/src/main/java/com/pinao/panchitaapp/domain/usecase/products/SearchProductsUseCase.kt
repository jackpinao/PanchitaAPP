package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class SearchProductsUseCase(private val repository: ProductRepository) {
    operator fun invoke(query: String): Flow<List<ProductModel>> {
        return repository.searchProducts(query)
    }
}