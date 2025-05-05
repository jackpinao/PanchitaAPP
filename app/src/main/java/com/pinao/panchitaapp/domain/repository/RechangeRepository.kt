package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.RechangeModel
import kotlinx.coroutines.flow.Flow

interface RechangeRepository {

    //Dao
    fun listAllDateRechangeFromDataBase(): Flow<List<RechangeModel>>
    fun listForDate(data: String): Flow<List<RechangeModel>>
    suspend fun save(rechangeModel: RechangeModel)
    suspend fun delete(rechangeModel: RechangeModel)

    //API
    suspend fun listAllDateRechangeFromApi(): List<RechangeModel>

}