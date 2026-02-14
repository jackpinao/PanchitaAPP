package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.CategoryEntity
import com.pinao.panchitaapp.domain.model.CategoryModel

object CategoryMapper {
    fun toDomain(entity: CategoryEntity): CategoryModel {
        return CategoryModel(
            categoryId = entity.categoryId,
            storeId = entity.storeId,
            name = entity.name,
            revenue = entity.revenue,
            isSynced = entity.isSynced == 1
        )
    }

    fun toDatabase(model: CategoryModel): CategoryEntity {
        return CategoryEntity(
            categoryId = model.categoryId,
            storeId = model.storeId,
            name = model.name,
            revenue = model.revenue,
            isSynced = if (model.isSynced) 1 else 0
        )
    }
}