package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.TicketEntity
import com.pinao.panchitaapp.domain.model.TicketModel

object TicketMapper {
    fun toDomain(entity: TicketEntity): TicketModel {
        return TicketModel(
            id = entity.id,
            date = entity.date,
            total = entity.total,
            state = entity.state,
            idUser = entity.idUser,
            idClient = entity.idClient,
        )
    }
    fun toEntity(model: TicketModel): TicketEntity {
        return TicketEntity(
            id = model.id,
            date = model.date,
            total = model.total,
            state = model.state,
            idUser = model.idUser,
            idClient = model.idClient,
        )
    }
}