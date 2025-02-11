package com.pinao.panchitaapp.data.repository

import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.mapper.RechangeMapper
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

class RechangeRepositoryImpl @Inject constructor (
    private val rechangeDao: RechangeDao
): RechangeRepository {

    override suspend fun save(rechange: Rechange) {
        if (rechange.id == 0) {
            rechangeDao.insert(RechangeMapper.toDatabase(rechange))
        } else {
            rechangeDao.update(RechangeMapper.toDatabase(rechange))
        }
    }

    override suspend fun delete(rechange: Rechange) {
        return rechangeDao.delete(RechangeMapper.toDatabase(rechange))
    }

    override suspend fun listDate(data: String): Flow<List<Rechange>> {
        return rechangeDao.listForDate(data).map {
            it.map { rechangeEntity ->
                RechangeMapper.toDomain(rechangeEntity)
            }
        }
    }

}