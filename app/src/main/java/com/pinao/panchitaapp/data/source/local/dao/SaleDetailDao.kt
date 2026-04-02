package com.pinao.panchitaapp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(toDatabase: SaleDetailEntity): Long
    @Query("SELECT * FROM sale_detail WHERE sale_id = :sale_id")
    fun getDetailsByTicketId(sale_id: String): Flow<List<SaleDetailEntity>>

}