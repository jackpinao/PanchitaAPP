package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.pinao.panchitaapp.data.mapper.BrandMapper
import com.pinao.panchitaapp.data.source.local.dao.BrandDao
import com.pinao.panchitaapp.data.source.remote.RemoteDataSource
import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.repository.BrandRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BrandRepositoryImpl(
    private val brandDao: BrandDao,
    private val remoteDataSource: RemoteDataSource
) : BrandRepository {
    override fun getAllBrands(): Flow<List<BrandModel>> {
        Log.d("BrandRepositoryImpl", "Getting all brands from Room")
        return brandDao.getAll().map { entities ->
            entities.map {
                BrandMapper.toDomain(it)
            }
        }
    }

    override fun getBrandById(id: Int): Flow<BrandModel?> {
        return brandDao.getBrandById(id).map { entity ->
            entity?.let { BrandMapper.toDomain(it) }
        }
    }

    override suspend fun saveBrand(brand: BrandModel) {
        withContext(Dispatchers.IO) {
            val isSync = remoteDataSource.brandRemoteDataSource.saveBrand(brand)
            if (isSync) {
                brand.isSynced = true
                brandDao.upsertAll(BrandMapper.toDatabase(brand))
            } else {
                brand.isSynced = false
                brandDao.upsertAll(BrandMapper.toDatabase(brand))
                Log.d("BrandRepositoryImpl", "Error saving brand to Firestore")
            }
        }
    }

    override suspend fun deleteBrand(brand: BrandModel) {
        withContext(Dispatchers.IO) {
            val isDeleted = remoteDataSource.brandRemoteDataSource.deleteBrand(brand)
            if (isDeleted) {
                brandDao.deleteAll(BrandMapper.toDatabase(brand))
            } else {
                Log.d("BrandRepositoryImpl", "Error deleting brand from Firestore")
            }
        }
    }
}
