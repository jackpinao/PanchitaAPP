package com.pinao.panchitaapp.domain.usecase.brand

import com.pinao.panchitaapp.domain.repository.BrandRepository

class GetAllBrandsUseCase(
    private val repository: BrandRepository
) {
    operator fun invoke() = repository.getAllBrands()
}
