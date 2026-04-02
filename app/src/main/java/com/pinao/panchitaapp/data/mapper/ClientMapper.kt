package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.ClientEntity
import com.pinao.panchitaapp.domain.model.ClientModel

object ClientMapper {
    fun toDomain(entity: ClientEntity): ClientModel {
        return ClientModel(
            id = entity.clientId,
            name = entity.name,
            numDoc = entity.numDoc,
            active = entity.isActive,
            dateCreate = entity.dateCreate,
        )
    }
    fun toEntity(model: ClientModel): ClientEntity {
        return ClientEntity(
            clientId = model.id,
            name = model.name,
            numDoc = model.numDoc,
            isActive = model.active,
            dateCreate = model.dateCreate,
        )
    }
}