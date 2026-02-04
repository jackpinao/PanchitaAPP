package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import kotlinx.coroutines.flow.Flow

interface TemporaryProductRepository {
    fun getTemporaryProducts(): Flow<List<TemporaryProductModel>>
    suspend fun saveTemporaryProduct(item: TemporaryProductModel)
    suspend fun deleteTemporaryProduct(item: TemporaryProductModel)
    suspend fun clearAllTemporaryProducts()
}