package com.pinao.panchitaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM product ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductsEntity>>

    @Query("SELECT * FROM product WHERE code = :codeProduct")
    fun findCodeProduct(codeProduct: String): Flow<ProductsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductsEntity): Long
    
    @Update
    suspend fun updateProduct(product: ProductsEntity): Int

    @Delete
    suspend fun deleteProduct(product: ProductsEntity)
}