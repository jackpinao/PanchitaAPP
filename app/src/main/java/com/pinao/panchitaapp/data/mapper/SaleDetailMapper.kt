package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleDetailModel
import java.util.UUID

object SaleDetailMapper {
    fun toEntity(product: ProductModel, saleId: String): SaleDetailEntity =
        SaleDetailEntity(
            saleDetailId = UUID.randomUUID().toString(),
            saleId = saleId,
            productId = product.productId,
            quantity = product.stockQuantity,
            priceAtSale = product.priceSell,
            subtotal = product.priceSell * product.stockQuantity
        )

    fun toDatabase(model: SaleDetailModel): SaleDetailEntity =
        SaleDetailEntity(
            saleDetailId = model.saleDetailId,
            saleId = model.saleId,
            productId = model.productId,
            quantity = model.quantity,
            priceAtSale = model.priceAtSale,
            subtotal = model.subtotal
        )

    fun toDomain(entity: SaleDetailEntity): SaleDetailModel =
        SaleDetailModel(
            saleDetailId = entity.saleDetailId,
            saleId = entity.saleId,
            productId = entity.productId,
            quantity = entity.quantity,
            priceAtSale = entity.priceAtSale,
            subtotal = entity.subtotal
        )
}
