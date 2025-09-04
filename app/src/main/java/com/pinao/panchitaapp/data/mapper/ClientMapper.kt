package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.ClientEntity
import com.pinao.panchitaapp.domain.model.ClientModel

object ClientMapper {
    fun toDomain(entity: ClientEntity): ClientModel {
        return ClientModel(
            id = entity.id,
            name = entity.name,
            numDoc = entity.numDoc,
            active = entity.active,
            dateCreate = entity.dateCreate,
        )
    }
    fun toEntity(model: ClientModel): ClientEntity {
        return ClientEntity(
            id = model.id,
            name = model.name,
            numDoc = model.numDoc,
            active = model.active,
            dateCreate = model.dateCreate,
        )
    }
}