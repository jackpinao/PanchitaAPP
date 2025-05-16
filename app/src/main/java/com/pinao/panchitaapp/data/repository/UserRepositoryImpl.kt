package com.pinao.panchitaapp.data.repository

import com.pinao.panchitaapp.data.local.dao.UserDao
import com.pinao.panchitaapp.data.mapper.UserMapper
import com.pinao.panchitaapp.domain.model.UserModel
import com.pinao.panchitaapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun save(userModel: UserModel): Int {
        return if (userModel.id == 0) {
            userDao.insert(UserMapper.toDatabase(userModel)).toInt()
        } else {
            userDao.update(UserMapper.toDatabase(userModel))
        }
    }

    override suspend fun delete(userModel: UserModel): Int {
        return userDao.delete(UserMapper.toDatabase(userModel))
    }

    override suspend fun updatePassword(id: Int, password: String): Int {
        return userDao.updatePassword(id, password)
    }

    override suspend fun getUser(email: String, password: String): UserModel? {
        return userDao.getUser(email, password)?.let { UserMapper.toDomain(it) }
    }

    override suspend fun getUserForId(id: Int): UserModel? {
        return userDao.getUserForId(id)?.let { UserMapper.toDomain(it) }
    }

    override suspend fun accountExists(): Int {
        return userDao.accountExists()
    }

    override suspend fun saveAccount(userModel: UserModel): UserModel? {
        return userDao.saveAccount(UserMapper.toDatabase(userModel))?.let { UserMapper.toDomain(it) }
    }

    override fun listDate(data: String): Flow<List<UserModel>> {
        return userDao.listForName(data).map { items ->
            items.map { userEntity ->
                UserMapper.toDomain(userEntity)
            }
        }
    }

}