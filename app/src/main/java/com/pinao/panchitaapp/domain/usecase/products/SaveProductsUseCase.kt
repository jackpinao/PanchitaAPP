package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.repository.ProductRepository

class SaveProductsUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productModel: ProductModel) = repository.saveProduct(productModel)
}