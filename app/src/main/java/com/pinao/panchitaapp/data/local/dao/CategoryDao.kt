package com.pinao.panchitaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.pinao.panchitaapp.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun insertCategory(toDatabase: CategoryEntity): Long

    @Query("SELECT * FROM category WHERE name = :codeCategory")
    fun findCodeCategory(codeCategory: String): Flow<CategoryEntity?>

    @Delete
    suspend fun deleteCategory(toDatabase: CategoryEntity)
}