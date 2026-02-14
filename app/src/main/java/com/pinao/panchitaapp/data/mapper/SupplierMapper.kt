package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.SupplierEntity
import com.pinao.panchitaapp.domain.model.SupplierModel

object SupplierMapper {
    fun toDomain(entity: SupplierEntity): SupplierModel {
        return SupplierModel(
            id = entity.supplierId,
            storeId = entity.storeId,
            name = entity.name,
            phone = entity.phone,
            email = entity.email,
            isSynced = entity.isSynced == 1
        )
}
    fun toDatabase(model: SupplierModel): SupplierEntity {
        return SupplierEntity(
            supplierId = model.id,
            storeId = model.storeId,
            name = model.name,
            phone = model.phone,
            email = model.email,
            isSynced = if (model.isSynced) 1 else 0
        )
    }
}