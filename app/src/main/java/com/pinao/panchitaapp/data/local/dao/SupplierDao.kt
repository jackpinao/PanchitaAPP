package com.pinao.panchitaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import com.pinao.panchitaapp.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {

    @Insert
    suspend fun insertSupplier(supplier: SupplierEntity)

    @Update
    suspend fun updateSupplier(supplier: SupplierEntity)

    @Delete
    suspend fun deleteSupplier(supplier: SupplierEntity)

    // Consultas reactivas con Flow
    @Query("SELECT * FROM Supplier")
    fun getAllSuppliers(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM Supplier WHERE supplier_id = :id")
    fun getSupplierById(id: String): Flow<SupplierEntity?>

    @Query("SELECT * FROM Supplier WHERE store_id = :storeId")
    fun getSuppliersByStore(storeId: String): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM Supplier WHERE is_synced = 0")
    fun getUnsyncedSuppliers(): Flow<List<SupplierEntity>>

    @Query("UPDATE Supplier SET is_synced = 1 WHERE supplier_id = :id")
    suspend fun markAsSynced(id: String)
}
