package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.BrandModel
import kotlinx.coroutines.flow.Flow

interface BrandRepository {
    fun getAllBrands(): Flow<List<BrandModel>>
    fun getBrandById(id: Int): Flow<BrandModel?>
    suspend fun saveBrand(brand: BrandModel)
    suspend fun deleteBrand(brand: BrandModel)
}
