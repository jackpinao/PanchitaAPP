package com.pinao.panchitaapp.data.repository

import com.pinao.panchitaapp.data.local.dao.TemporaryProductDao
import com.pinao.panchitaapp.data.mapper.TemporaryProductMapper
import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import com.pinao.panchitaapp.domain.repository.TemporaryProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TemporaryProductRepositoryImpl(
    private val temporaryProductDao: TemporaryProductDao
) : TemporaryProductRepository {

    override fun getTemporaryProducts(): Flow<List<TemporaryProductModel>> {
        return temporaryProductDao.getAllTemporaryProducts().map { entities ->
            entities.map { TemporaryProductMapper.toDomain(it) }
        }
    }

    override suspend fun saveTemporaryProduct(item: TemporaryProductModel) {
        temporaryProductDao.insert(TemporaryProductMapper.toEntity(item))
    }

    override suspend fun deleteTemporaryProduct(item: TemporaryProductModel) {
        temporaryProductDao.delete(TemporaryProductMapper.toEntity(item))
    }

    override suspend fun clearAllTemporaryProducts() {
        temporaryProductDao.clearAll()
    }
}