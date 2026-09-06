package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.pinao.panchitaapp.data.mapper.SaleDetailMapper
import com.pinao.panchitaapp.data.mapper.SaleMapper
import com.pinao.panchitaapp.data.network.pos.SaleApiClient
import com.pinao.panchitaapp.data.source.local.dao.SaleDao
import com.pinao.panchitaapp.data.source.local.dao.SaleDetailDao
import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.data.source.local.entity.SaleEntity
import com.pinao.panchitaapp.data.source.remote.dto.SaleItemRequestDto
import com.pinao.panchitaapp.data.source.remote.dto.SaleRequestDto
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.repository.SaleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio de tickets con persistencia local offline-first y sincronización con la API POS.
 */
class SaleRepositoryImpl(
    private val saleDao: SaleDao,
    private val saleDetailDao: SaleDetailDao,
    private val saleApiClient: SaleApiClient
) : SaleRepository {

    override suspend fun saveFullSale(ticket: SaleModel, products: List<ProductModel>) {
        withContext(Dispatchers.IO) {
            try {
                val saleEntity = SaleMapper.toEntity(ticket).copy(isSynced = 0)
                val detailEntities = products.map { product ->
                    SaleDetailMapper.toEntity(product, ticket.saleId)
                }

                // La venta permanece pendiente hasta que la API confirme el procesamiento.
                saleDao.saveFullSale(saleEntity, detailEntities)
                Log.d(TAG, "Venta guardada localmente en Room")
                syncSale(saleEntity, detailEntities)
            } catch (e: Exception) {
                Log.e(TAG, "Error crítico al guardar la venta: ${e.message}", e)
                throw e
            }
        }
    }

    override suspend fun syncUnsyncedSales() {
        withContext(Dispatchers.IO) {
            saleDao.getUnsyncedSales().forEach { saleEntity ->
                try {
                    val detailEntities = saleDetailDao.getDetailsBySaleId(saleEntity.saleId)
                    if (detailEntities.isEmpty()) {
                        Log.w(TAG, "Venta pendiente sin detalles: ${saleEntity.saleId}")
                    } else {
                        syncSale(saleEntity, detailEntities)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "No se pudo reintentar la venta ${saleEntity.saleId}: ${e.message}")
                }
            }
        }
    }

    private suspend fun syncSale(
        saleEntity: SaleEntity,
        detailEntities: List<SaleDetailEntity>
    ) {
        try {
            val request = SaleRequestDto(
                items = detailEntities.map { detail ->
                    SaleItemRequestDto(
                        productId = detail.productId,
                        quantity = detail.quantity,
                        unitPrice = detail.priceAtSale,
                        discount = 0.0
                    )
                },
                paymentMethod = normalizePaymentType(saleEntity.paymentType),
                customerId = saleEntity.clientId.takeIf { it.isNotBlank() },
                amountPaid = saleEntity.totalAmount,
                notes = "Venta registrada desde PanchitaApp",
                offlineId = saleEntity.saleId
            )

            val response = saleApiClient.processSale(
                tenantId = saleEntity.storeId,
                userId = saleEntity.userId,
                request = request
            )

            if (response.isSuccessful) {
                saleDao.updateSyncStatus(saleEntity.saleId, 1)
                Log.d(TAG, "Venta ${saleEntity.saleId} sincronizada exitosamente")
            } else {
                Log.w(
                    TAG,
                    "Error en API POS para ${saleEntity.saleId}; permanece pendiente: " +
                        response.errorBody()?.string()
                )
            }
        } catch (e: Exception) {
            Log.w(
                TAG,
                "Error de red al sincronizar ${saleEntity.saleId}; permanece pendiente: ${e.message}"
            )
        }
    }

    private fun normalizePaymentType(paymentType: String): String = when (paymentType.lowercase()) {
        "efectivo", "cash" -> "cash"
        "tarjeta", "card" -> "card"
        "yape" -> "yape"
        "plin" -> "plin"
        "credito", "credit" -> "credit"
        "transferencia", "transfer" -> "transfer"
        else -> "cash"
    }

    private companion object {
        const val TAG = "SaleRepositoryImpl"
    }
}
