package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun save(userModel: UserModel): Int
    suspend fun delete(userModel: UserModel): Int
    //suspend fun updatePassword(id: String, password: String): Int
    suspend fun getUser(email:String): UserModel?
    suspend fun getUserForId(id: String): UserModel?
    suspend fun accountExists(): Int
    suspend fun saveAccount(userModel: UserModel): UserModel?
    fun listDate(data: String): Flow<List<UserModel>>
}