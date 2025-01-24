package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.Rechange
import kotlinx.coroutines.flow.Flow

interface RechangeRepository {
    suspend fun save(rechange: Rechange): Long
    suspend fun delete(rechange: Rechange): Int
    suspend fun listDate(data: String): Flow<List<Rechange>>
}