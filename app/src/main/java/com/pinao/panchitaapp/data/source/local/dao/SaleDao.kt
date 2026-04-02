package com.pinao.panchitaapp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.data.source.local.entity.SaleEntity

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(saleEntity: SaleEntity)// Necesitamos acceso al DAO de detalles o definir la inserción aquí
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: List<SaleDetailEntity>)

    @Transaction
    suspend fun saveFullSale(saleEntity: SaleEntity, details: List<SaleDetailEntity>) {
        insertTicket(saleEntity)
        insertDetails(details)
    }
}