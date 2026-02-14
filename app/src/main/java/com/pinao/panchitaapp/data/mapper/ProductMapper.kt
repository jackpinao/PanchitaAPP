package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import com.pinao.panchitaapp.domain.model.ProductModel

object ProductMapper {
    fun toDomain(entity: ProductsEntity): ProductModel {
        return ProductModel(
            productId = entity.productId,
            storeId = entity.storeId,
            categoryId = entity.categoryId,
            brandId = entity.brandId,
            detailTicketEntityId = entity.detailTicketEntityId,
            name = entity.name,
            priceSell = entity.priceSell,
            priceBuy = entity.priceBuy,
            priceExcludingIGV = entity.priceExcludingIGV,
            description = entity.description,
            image = entity.image,
            stockQuantity = entity.stockQuantity,
            barcode = entity.barcode,
            lastUpdated = entity.lastUpdated,
            stockMin = entity.stockMin,
            isSynced = entity.isSynced == 1
        )
    }

    fun toDatabase(model: ProductModel): ProductsEntity {
        return ProductsEntity(
            productId = model.productId,
            storeId = model.storeId,
            categoryId = model.categoryId,
            brandId = model.brandId,
            detailTicketEntityId = model.detailTicketEntityId.toString(),
            name = model.name,
            priceSell = model.priceSell,
            priceBuy = model.priceBuy,
            priceExcludingIGV = model.priceExcludingIGV,
            description = model.description,
            image = model.image,
            stockQuantity = model.stockQuantity,
            barcode = model.barcode,
            lastUpdated = model.lastUpdated,
            stockMin = model.stockMin,
            isSynced = if (model.isSynced) 1 else 0
        )
    }
}
