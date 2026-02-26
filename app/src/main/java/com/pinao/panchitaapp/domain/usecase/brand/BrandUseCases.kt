package com.pinao.panchitaapp.domain.usecase.brand

class BrandUseCases (
    val getAll: GetAllBrandsUseCase,
    val findByCode: FindBrandUseCase,
    val save: SaveBrandUseCase,
    val delete: DeleteBrandUseCase,
)