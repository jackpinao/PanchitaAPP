package com.pinao.panchitaapp.domain.usecase.brand

import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.repository.BrandRepository

class SaveBrandUseCase(
    private val repository: BrandRepository
) {
    suspend operator fun invoke(brandModel: BrandModel) = repository.saveBrand(brandModel)
}
