package com.pinao.panchitaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.pinao.panchitaapp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity): Int

    @Delete
    suspend fun delete(user: UserEntity): Int

    @Query("SELECT * FROM user WHERE name LIKE '%' || :name || '%'")
    fun listForName(name: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE active=1 AND email = :email AND password = :password")
    suspend fun getUser(email: String, password: String): UserEntity?

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUserForId(id: Int): UserEntity?

    @Query("SELECT ifnull(count(id),0) FROM user")
    suspend fun accountExists(): Int

    @Query("UPDATE user SET password = :password WHERE id = :id")
    suspend fun updatePassword(id: Int, password: String): Int

    @Transaction
    suspend fun saveAccount(user: UserEntity): UserEntity? {
        return getUserForId(
            insert(user).toInt()
        )
    }
}