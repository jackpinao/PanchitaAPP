package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.CategoryEntity
import com.pinao.panchitaapp.data.source.remote.dto.FirebaseCategoryDto
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

    fun toDomain(dto: FirebaseCategoryDto): CategoryModel {
        return CategoryModel(
            categoryId = dto.categoryId,
            storeId = dto.storeId,
            name = dto.name,
            revenue = dto.revenue,
            isSynced = true
        )
    }

    fun toCategoryDto(model: CategoryModel): FirebaseCategoryDto {
        return FirebaseCategoryDto(
            categoryId = model.categoryId,
            storeId = model.storeId,
            name = model.name,
            revenue = model.revenue
        )
    }
}