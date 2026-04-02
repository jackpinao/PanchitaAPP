package com.pinao.panchitaapp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pinao.panchitaapp.data.source.local.entity.RechangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RechangeDao {

    @Query("SELECT * FROM rechange ORDER BY date DESC")
    fun getAllDateRechange(): Flow<List<RechangeEntity>>

    @Query("SELECT * FROM rechange WHERE date LIKE '%' || :date || '%' ORDER BY date DESC")
    fun getListForDate(date: String): Flow<List<RechangeEntity>>

    @Query("SELECT * FROM rechange WHERE date LIKE '%' || :date || '%' ORDER BY date DESC")
    suspend fun getListForDate2(date: String): RechangeEntity

    @Query("DELETE FROM rechange")
    suspend fun deleteAllRechanges()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rechange: RechangeEntity): Long

//    @Update
//    suspend fun update(rechange: RechangeEntity)

    @Delete
    suspend fun delete(rechange: RechangeEntity)

}