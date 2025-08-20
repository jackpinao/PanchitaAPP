package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.data.repository.ProductsRepositoryImpl

class GetAllProductsUseCase(
    private val repository: ProductsRepositoryImpl
) {

    operator fun invoke() = repository.getAllProductsFromDataBase()
}