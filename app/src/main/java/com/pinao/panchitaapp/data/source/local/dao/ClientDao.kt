package com.pinao.panchitaapp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pinao.panchitaapp.data.source.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM client")
    fun getClients(): Flow<List<ClientEntity>>
    @Query("SELECT * FROM client WHERE client_id = :id")
    fun getById(id: String): Flow<ClientEntity>
    @Query("SELECT * FROM client WHERE name LIKE :name")
    fun getByName(name: String): Flow<List<ClientEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: ClientEntity)
    @Update
    suspend fun update(client: ClientEntity)
    @Delete
    suspend fun delete(client: ClientEntity)

}