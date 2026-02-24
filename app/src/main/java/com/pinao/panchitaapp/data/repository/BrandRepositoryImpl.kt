package com.pinao.panchitaapp.data.repository

import com.pinao.panchitaapp.data.local.dao.BrandDao
import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.repository.BrandRepository
import kotlinx.coroutines.flow.Flow

class BrandRepositoryImpl(
    private val brandDao: BrandDao
) : BrandRepository {
    override fun getAllBrands(): Flow<List<BrandModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveBrand(brand: BrandModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBrand(brand: BrandModel) {
        TODO("Not yet implemented")
    }
}