package com.pinao.panchitaapp.data.source.remote

import com.pinao.panchitaapp.domain.model.BrandModel

interface BrandRemoteDataSource {
    suspend fun getBrands(): List<BrandModel>
    suspend fun saveBrand(brand: BrandModel): Boolean
    suspend fun deleteBrand(brand: BrandModel): Boolean
}