package com.pinao.panchitaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.pinao.panchitaapp.data.local.entity.BrandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrandDao {
    @Query("SELECT * FROM brand")
    fun getAll(): Flow<List<BrandEntity>>

    @Upsert
    suspend fun upsertAll(brand: BrandEntity)

    @Query("SELECT * FROM brand WHERE brand_id = :brand_id")
    suspend fun getBrandById(brand_id: Int): Flow<BrandEntity?>

    @Delete
    suspend fun deleteAll(brand: BrandEntity)

}