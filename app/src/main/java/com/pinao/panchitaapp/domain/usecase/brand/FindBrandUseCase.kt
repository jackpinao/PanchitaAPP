package com.pinao.panchitaapp.domain.usecase.brand

import com.pinao.panchitaapp.domain.repository.BrandRepository

class FindBrandUseCase(
    private val repository: BrandRepository
) {
    operator fun invoke(id: Int) = repository.getBrandById(id)
}
