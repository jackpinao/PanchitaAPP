package com.pinao.panchitaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.pinao.panchitaapp.data.local.entity.DetailTicketEntity
import com.pinao.panchitaapp.data.local.entity.TicketEntity

@Dao
interface TicketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)// Necesitamos acceso al DAO de detalles o definir la inserción aquí
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: List<DetailTicketEntity>)

    @Transaction
    suspend fun saveFullSale(ticket: TicketEntity, details: List<DetailTicketEntity>) {
        insertTicket(ticket)
        insertDetails(details)
    }
}