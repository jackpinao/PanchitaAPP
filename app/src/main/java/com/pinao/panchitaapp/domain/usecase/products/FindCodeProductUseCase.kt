package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.data.repository.ProductsRepositoryImpl

class FindCodeProductUseCase(
    private val repository: ProductsRepositoryImpl
) {
    operator fun invoke(codeProduct: String) = repository.findCodeProduct(codeProduct)
}