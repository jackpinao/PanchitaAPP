package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.UserEntity
import com.pinao.panchitaapp.domain.model.UserModel

object UserMapper {

    fun toDomain(entity: UserEntity): UserModel {
        return UserModel(
            userId = entity.userId,
            storeId = entity.storeId,
            name = entity.name,
            email = entity.email,
            password = entity.password,
            role = entity.role,
            active = entity.isActive == 1
        )
    }

    fun toDatabase(model: UserModel): UserEntity {
        return UserEntity(
            userId = model.userId,
            storeId = model.storeId,
            name = model.name,
            email = model.email,
            password = model.password,
            role = model.role,
            isActive = if (model.active) 1 else 0
        )
    }
}