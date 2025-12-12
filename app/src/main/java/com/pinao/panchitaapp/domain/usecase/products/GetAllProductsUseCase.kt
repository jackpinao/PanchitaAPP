package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.ProductRepository

class GetAllProductsUseCase(
    private val repository: ProductRepository
) {

    operator fun invoke() = repository.getAllProductsFromDataBase()
}