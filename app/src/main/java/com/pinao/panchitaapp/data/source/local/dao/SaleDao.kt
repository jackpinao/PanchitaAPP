package com.pinao.panchitaapp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.data.source.local.entity.SaleEntity

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(saleEntity: SaleEntity)// Necesitamos acceso al DAO de detalles o definir la inserción aquí

    @Query("SELECT * FROM sale WHERE is_synced = 0 ORDER BY sale_date ASC")
    suspend fun getUnsyncedSales(): List<SaleEntity>

    @Query("UPDATE sale SET is_synced = :isSynced WHERE sale_id = :saleId")
    suspend fun updateSyncStatus(saleId: String, isSynced: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: List<SaleDetailEntity>)

    @Transaction
    suspend fun saveFullSale(saleEntity: SaleEntity, details: List<SaleDetailEntity>) {
        insertTicket(saleEntity)
        insertDetails(details)
    }
}
