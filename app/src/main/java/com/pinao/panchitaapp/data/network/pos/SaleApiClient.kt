package com.pinao.panchitaapp.data.network.pos

import com.pinao.panchitaapp.data.source.remote.dto.SaleRequestDto
import com.pinao.panchitaapp.data.source.remote.dto.SaleResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SaleApiClient {
    @POST("api/pos/sale")
    suspend fun processSale(
        @Header("x-tenant-id") tenantId: String,
        @Header("x-user-id") userId: String,
        @Body request: SaleRequestDto
    ): Response<SaleResponseDto>
}
