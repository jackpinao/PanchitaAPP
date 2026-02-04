package com.pinao.panchitaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pinao.panchitaapp.data.local.entity.DetailTicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailTicketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(toDatabase: DetailTicketEntity): Long
    @Query("SELECT * FROM detail_ticket WHERE idTicket = :ticketId")
    fun getDetailsByTicketId(ticketId: String): Flow<List<DetailTicketEntity>>

}