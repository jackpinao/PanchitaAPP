package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import com.pinao.panchitaapp.domain.model.Rechange

object RechangeMapper {

    fun toDomain(entity: RechangeEntity): Rechange {
        return Rechange(
            id = entity.id,
            date = entity.date,
            amount = entity.amount,
            numPhone = entity.numPhone
        )
    }

    fun toDatabase(model: Rechange): RechangeEntity {
        return RechangeEntity(
            id = model.id,
            date = model.date,
            amount = model.amount,
            numPhone = model.numPhone
        )
    }
}