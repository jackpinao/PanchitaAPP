package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.BrandEntity
import com.pinao.panchitaapp.data.source.remote.dto.FirebaseBrandDto
import com.pinao.panchitaapp.data.source.remote.dto.SupabaseBrandDto
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

    fun toDomain(dto: FirebaseBrandDto): BrandModel {
        return BrandModel(
            brandId = dto.brandId,
            storeId = dto.storeId,
            name = dto.name,
            isSynced = true
        )
    }

    fun toBrandDto(model: BrandModel): FirebaseBrandDto {
        return FirebaseBrandDto(
            brandId = model.brandId,
            storeId = model.storeId,
            name = model.name
        )
    }

    fun toDomain(dto: SupabaseBrandDto): BrandModel {
        return BrandModel(
            brandId = dto.brandId,
            storeId = dto.storeId,
            name = dto.name,
            isSynced = true
        )
    }

    fun toSupabaseDto(model: BrandModel): SupabaseBrandDto {
        return SupabaseBrandDto(
            brandId = model.brandId,
            storeId = model.storeId,
            name = model.name
        )
    }
}
