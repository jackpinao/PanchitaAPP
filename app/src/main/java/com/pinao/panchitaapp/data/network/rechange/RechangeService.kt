package com.pinao.panchitaapp.data.network.rechange

import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import com.pinao.panchitaapp.domain.model.RechangeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RechangeService (private val api:RechangeApiClient) {
    suspend fun getAllRechanges(): List<RechangeEntity> {
        return withContext(Dispatchers.IO) {
            val response = api.getAllRechanges()
            response.body() ?: emptyList()
        }
    }
}