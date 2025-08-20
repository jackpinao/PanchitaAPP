package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.data.repository.ProductsRepositoryImpl
import com.pinao.panchitaapp.domain.model.ProductModel

class SaveProductsUseCase(
    private val repository: ProductsRepositoryImpl
) {
    suspend operator fun invoke(productModel: ProductModel) = repository.saveProduct(productModel)
}