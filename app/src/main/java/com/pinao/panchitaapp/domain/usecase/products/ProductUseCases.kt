package com.pinao.panchitaapp.domain.usecase.products

data class ProductUseCases(
    val getAll: GetAllProductsUseCase,
    val findByCode: FindCodeProductUseCase,
    val save: SaveProductsUseCase,
    val delete: DeleteProductUseCase,
    val refreshProducts: RefreshProductsUseCase,
    val search: SearchProductsUseCase,
    val syncUnsyncedProducts: SyncUnsyncedProductsUseCase
)
