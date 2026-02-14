package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.BrandEntity
import com.pinao.panchitaapp.domain.model.BrandModel

object BrandMapper {
    fun toDomain(entity: BrandEntity): BrandModel {
        return BrandModel(
            brandId = entity.brandId,
            storeId = entity.storeId,
            name = entity.name,
            isSynced = entity.isSynced == 1
        )

    }

    fun toDatabase(model: BrandModel): BrandEntity {
        return BrandEntity(
            brandId = model.brandId,
            storeId = model.storeId,
            name = model.name,
            isSynced = if (model.isSynced) 1 else 0
        )
    }
}
