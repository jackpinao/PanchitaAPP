package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.pinao.panchitaapp.data.local.dao.BrandDao
import com.pinao.panchitaapp.data.mapper.BrandMapper
import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.repository.BrandRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BrandRepositoryImpl(
    private val brandDao: BrandDao
) : BrandRepository {
    override fun getAllBrands(): Flow<List<BrandModel>> {
        return brandDao.getAll().map { entities ->
            entities.map { BrandMapper.toDomain(it) }
        }
    }

    override fun getBrandById(id: Int): Flow<BrandModel?> {
        return brandDao.getBrandById(id).map { entity ->
            entity?.let { BrandMapper.toDomain(it) }
        }
    }

    override suspend fun saveBrand(brand: BrandModel) {
        withContext(Dispatchers.IO) {
            try {
                brandDao.upsertAll(BrandMapper.toDatabase(brand))
            } catch (e: Exception) {
                Log.e("BrandRepositoryImpl", "Error saving brand", e)
            }
        }
    }

    override suspend fun deleteBrand(brand: BrandModel) {
        withContext(Dispatchers.IO) {
            try {
                brandDao.deleteAll(BrandMapper.toDatabase(brand))
            } catch (e: Exception) {
                Log.e("BrandRepositoryImpl", "Error deleting brand", e)
            }
        }
    }
}
