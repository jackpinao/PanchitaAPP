package com.pinao.panchitaapp.data.repository

import com.pinao.panchitaapp.data.local.dao.UserDao
import com.pinao.panchitaapp.data.mapper.UserMapper
import com.pinao.panchitaapp.domain.model.User
import com.pinao.panchitaapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun save(user: User): Int {
        return if (user.id == 0) {
            userDao.insert(UserMapper.toDatabase(user)).toInt()
        } else {
            userDao.update(UserMapper.toDatabase(user))
        }
    }

    override suspend fun delete(user: User): Int {
        return userDao.delete(UserMapper.toDatabase(user))
    }

    override suspend fun updatePassword(id: Int, password: String): Int {
        return userDao.updatePassword(id, password)
    }

    override suspend fun getUser(email: String, password: String): User? {
        return userDao.getUser(email, password)?.let { UserMapper.toDomain(it) }
    }

    override suspend fun getUserForId(id: Int): User? {
        return userDao.getUserForId(id)?.let { UserMapper.toDomain(it) }
    }

    override suspend fun accountExists(): Int {
        return userDao.accountExists()
    }

    override suspend fun saveAccount(user: User): User? {
        return userDao.saveAccount(UserMapper.toDatabase(user))?.let { UserMapper.toDomain(it) }
    }

    override fun listDate(data: String): Flow<List<User>> {
        return userDao.listForName(data).map {
            it.map { userEntity ->
                UserMapper.toDomain(userEntity)
            }
        }
    }

}