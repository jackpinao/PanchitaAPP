package com.pinao.panchitaapp.data.network.rechange

import com.pinao.panchitaapp.data.source.local.entity.RechangeEntity
import retrofit2.Response
import retrofit2.http.GET

interface RechangeApiClient {
    @GET("/.json")
    suspend fun getAllRechanges(): Response<List<RechangeEntity>>
}