package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.ProductRepository

class FindCodeProductUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(codeProduct: String) = repository.findCodeProduct(codeProduct)
}