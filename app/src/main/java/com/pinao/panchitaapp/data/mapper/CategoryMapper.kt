package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.CategoryEntity
import com.pinao.panchitaapp.domain.model.CategoryModel

object CategoryMapper {
    fun toDomain(entity: CategoryEntity): CategoryModel {
        return CategoryModel(
            id = entity.id,
            name = entity.name,
            revenue = entity.revenue
        )
    }
    fun toDatabase(model: CategoryModel): CategoryEntity {
        return CategoryEntity(
            id = model.id,
            name = model.name,
            revenue = model.revenue
        )
    }
}