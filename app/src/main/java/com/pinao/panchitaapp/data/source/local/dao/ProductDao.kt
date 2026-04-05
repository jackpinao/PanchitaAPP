package com.pinao.panchitaapp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.pinao.panchitaapp.data.source.local.entity.ProductWithCategory
import com.pinao.panchitaapp.data.source.local.entity.ProductsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM product WHERE is_deleted = 0 ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductsEntity>>

    @Transaction
    @Query("SELECT * FROM product WHERE is_deleted = 0")
    fun getProductsWithCategory(): Flow<List<ProductWithCategory>>

    @Query("SELECT * FROM product WHERE barcode = :codeProduct AND is_deleted = 0")
    fun findCodeProduct(codeProduct: String): Flow<ProductsEntity?>

    @Upsert
    suspend fun insertProduct(product: ProductsEntity): Long
    
    @Update
    suspend fun updateProduct(product: ProductsEntity): Int

    @Delete
    suspend fun deleteProduct(product: ProductsEntity)

    @Query("SELECT * FROM product WHERE barcode = :string AND is_deleted = 0")
    fun getProductForCode(string: String) : ProductsEntity?

    /**
     * Busca productos por nombre o código ignorando mayúsculas/minúsculas y que no estén borrados lógicamente.
     */
    @Query("SELECT * FROM product WHERE is_deleted = 0 AND (name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%') ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<ProductsEntity>>

    @Query("DELETE FROM product WHERE product_id NOT IN (:ids)")
    suspend fun deleteProductsNotInList(ids: List<String>)

    @Query("DELETE FROM product")
    suspend fun deleteAllProducts()

    @Query("SELECT * FROM product WHERE is_synced = 0 AND is_deleted = 0")
    suspend fun getUnsyncedProducts(): List<ProductsEntity>

    @Query("SELECT * FROM product WHERE is_deleted = 1")
    suspend fun getPendingDeletedProducts(): List<ProductsEntity>
}
