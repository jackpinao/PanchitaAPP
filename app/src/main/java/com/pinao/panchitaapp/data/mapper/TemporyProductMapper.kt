package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.TemporaryProductEntity
import com.pinao.panchitaapp.domain.model.TemporaryProductModel

object TemporaryProductMapper {
    fun toDomain(entity: TemporaryProductEntity) = TemporaryProductModel(
        id = entity.id,
        productId = entity.productId,
        name = entity.name,
        code = entity.code,
        price = entity.price,
        quantity = entity.quantity,
        priceExcludingIGV = entity.priceExcludingIGV
    )

    fun toEntity(model: TemporaryProductModel) = TemporaryProductEntity(
        id = model.id,
        productId = model.productId,
        name = model.name,
        code = model.code,
        price = model.price,
        quantity = model.quantity,
        priceExcludingIGV = model.priceExcludingIGV
    )
}