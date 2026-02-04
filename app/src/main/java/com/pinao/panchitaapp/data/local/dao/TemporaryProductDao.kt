package com.pinao.panchitaapp.data.local.dao

import androidx.room.*
import com.pinao.panchitaapp.data.local.entity.TemporaryProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemporaryProductDao {
    @Query("SELECT * FROM temporary_product")
    fun getAllTemporaryProducts(): Flow<List<TemporaryProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: TemporaryProductEntity)

    @Delete
    suspend fun delete(product: TemporaryProductEntity)

    @Query("DELETE FROM temporary_product")
    suspend fun clearAll()
}