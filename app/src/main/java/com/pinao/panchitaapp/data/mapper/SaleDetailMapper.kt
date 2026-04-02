package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.domain.model.SaleDetailModel

object SaleDetailMapper {
    fun toDomain(entity: SaleDetailEntity): SaleDetailModel {
        return SaleDetailModel(
            saleDetailId = entity.saleDetailId,
            saleId = entity.saleId,
            productId = entity.productId,
            quantity = entity.quantity,
            priceAtSale = entity.priceAtSale,
            subtotal = entity.subtotal
        )
    }
    fun toDatabase(model: SaleDetailModel): SaleDetailEntity {
        return SaleDetailEntity(
            saleDetailId = model.saleDetailId,
            saleId = model.saleId,
            productId = model.productId,
            quantity = model.quantity,
            priceAtSale = model.priceAtSale,
            subtotal = model.subtotal
        )
    }
}