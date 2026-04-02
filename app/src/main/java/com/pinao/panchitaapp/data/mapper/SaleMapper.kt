package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.SaleEntity
import com.pinao.panchitaapp.domain.model.SaleModel

object SaleMapper {
    fun toDomain(entity: SaleEntity): SaleModel {
        return SaleModel(
            saleId = entity.saleId,
            storeId = entity.storeId,
            userId = entity.userId,
            clientId = entity.clientId,
            saleDate = entity.saleDate,
            totalAmount = entity.totalAmount,
            paymentType = entity.paymentType,
            isSynced = entity.isSynced == 1,

        )
    }
    fun toEntity(model: SaleModel): SaleEntity {
        return SaleEntity(
            saleId = model.saleId,
            storeId = model.storeId,
            userId = model.userId,
            clientId = model.clientId,
            saleDate = model.saleDate,
            totalAmount = model.totalAmount,
            paymentType = model.paymentType,
            isSynced = if(model.isSynced) 1 else 0,
        )
    }
}