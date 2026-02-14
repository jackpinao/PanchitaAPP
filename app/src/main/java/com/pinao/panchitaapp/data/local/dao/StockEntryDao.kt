package com.pinao.panchitaapp.data.local.dao

import androidx.room.*
import com.pinao.panchitaapp.data.local.entity.StockEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de acceso a datos para la tabla StockEntry.
 */
@Dao
interface StockEntryDao {

    /**
     * Inserta o actualiza una entrada de stock.
     */
    @Upsert
    suspend fun upsertStockEntry(entry: StockEntryEntity)

    /**
     * Obtiene una entrada de stock por su ID.
     */
    @Query("SELECT * FROM StockEntry WHERE entry_id = :id")
    suspend fun getStockEntryById(id: String): StockEntryEntity?

    /**
     * Obtiene todas las entradas de stock, observando cambios mediante Flow.
     */
    @Query("SELECT * FROM StockEntry ORDER BY entry_date DESC")
    fun getAllStockEntries(): Flow<List<StockEntryEntity>>

    /**
     * Obtiene las entradas que aún no han sido sincronizadas.
     */
    @Query("SELECT * FROM StockEntry WHERE is_synced = 0")
    suspend fun getUnsyncedEntries(): List<StockEntryEntity>

    /**
     * Elimina una entrada de stock.
     */
    @Delete
    suspend fun deleteStockEntry(entry: StockEntryEntity)

    /**
     * Actualiza el estado de sincronización de una entrada.
     */
    @Query("UPDATE StockEntry SET is_synced = :isSynced WHERE entry_id = :id")
    suspend fun updateSyncStatus(id: String, isSynced: Int)
}