package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.UserEntity
import com.pinao.panchitaapp.domain.model.UserModel

object UserMapper {

    fun toDomain(
        entity: UserEntity
    ): UserModel {
        return UserModel(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            password = entity.password,
            active = entity.active
        )
    }
    fun toDatabase(
        model: UserModel
    ): UserEntity {
        return UserEntity(
            id = model.id,
            name = model.name,
            email = model.email,
            password = model.password,
            active = model.active
        )
    }
}