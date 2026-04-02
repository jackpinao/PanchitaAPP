package com.pinao.panchitaapp.data.network.rechange

import com.pinao.panchitaapp.data.source.local.entity.RechangeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RechangeService(private val api: RechangeApiClient) {
    suspend fun getAllRechanges(): List<RechangeEntity> {
        return withContext(Dispatchers.IO) {
            val response = api.getAllRechanges()
            response.body() ?: emptyList()
        }
    }
}