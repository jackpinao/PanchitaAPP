package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.DetailTicketEntity
import com.pinao.panchitaapp.domain.model.DetailTicketModel

object DetailsTicketMapper {
    fun toDomain(entity: DetailTicketEntity): DetailTicketModel {
        return DetailTicketModel(
            id = entity.id,
            date = entity.date,
            description = entity.description,
            price = entity.price,
            amount = entity.amount,
            import = entity.import,
            idTicket = entity.idTicket,
            idProduct = entity.idProduct
        )
    }
    fun toDatabase(model: DetailTicketModel): DetailTicketEntity {
        return DetailTicketEntity(
            id = model.id,
            date = model.date,
            description = model.description,
            price = model.price,
            amount = model.amount,
            import = model.import,
            idTicket = model.idTicket,
            idProduct = model.idProduct
        )
    }
}