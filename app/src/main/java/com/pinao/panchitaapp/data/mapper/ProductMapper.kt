package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import com.pinao.panchitaapp.domain.model.ProductModel

object ProductMapper {
    fun toDomain(entity: ProductsEntity): ProductModel {
        return ProductModel(
            id = entity.id,
            name = entity.name,
            sellingPrice = entity.sellingPrice,
            purchasePrice = entity.purchasePrice,
            priceExcludingIGV = entity.priceExcludingIGV,
            description = entity.description,
            image = entity.image,
            idCategory = entity.idCategory,
            stock = entity.stock,
            code = entity.code,
            idDetailTicketEntity = entity.idDetailTicketEntity
        )
    }

    fun toDatabase(model: ProductModel): ProductsEntity {
        return ProductsEntity(
            id = model.id,
            name = model.name,
            sellingPrice = model.sellingPrice,
            purchasePrice = model.purchasePrice,
            priceExcludingIGV = model.priceExcludingIGV,
            description = model.description,
            image = model.image,
            idCategory = model.idCategory,
            stock = model.stock,
            code = model.code,
            idDetailTicketEntity = model.idDetailTicketEntity.toString()
        )
    }
}
