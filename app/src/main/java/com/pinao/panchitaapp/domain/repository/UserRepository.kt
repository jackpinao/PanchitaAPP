package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun save(user: User): Int
    suspend fun delete(user: User): Int
    suspend fun updatePassword(id: Int, password: String): Int
    suspend fun getUser(email:String, password: String): User?
    suspend fun getUserForId(id: Int): User?
    suspend fun accountExists(): Int
    suspend fun saveAccount(user: User): User?
    fun listDate(data: String): Flow<List<User>>
}