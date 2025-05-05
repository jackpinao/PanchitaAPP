package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun save(userModel: UserModel): Int
    suspend fun delete(userModel: UserModel): Int
    suspend fun updatePassword(id: Int, password: String): Int
    suspend fun getUser(email:String, password: String): UserModel?
    suspend fun getUserForId(id: Int): UserModel?
    suspend fun accountExists(): Int
    suspend fun saveAccount(userModel: UserModel): UserModel?
    fun listDate(data: String): Flow<List<UserModel>>
}