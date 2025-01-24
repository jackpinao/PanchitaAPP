package com.pinao.panchitaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Dao
interface RechangeDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rechange: RechangeEntity): Long {
        return 0
    }

    @Update
    suspend fun update(rechange: RechangeEntity): Int {
        return 0
    }

    @Delete
    suspend fun delete(rechange: RechangeEntity): Int {
        return 0
    }

    @Query("SELECT * FROM rechange WHERE date LIKE '%' || :date || '%'")
    fun listForDate(date: String): Flow<List<RechangeEntity>> {
        return emptyFlow()
    }

}