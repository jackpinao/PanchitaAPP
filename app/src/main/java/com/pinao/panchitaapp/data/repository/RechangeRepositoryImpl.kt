package com.pinao.panchitaapp.data.repository

import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import com.pinao.panchitaapp.data.mapper.RechangeMapper
import com.pinao.panchitaapp.data.network.rechange.RechangeService
import com.pinao.panchitaapp.domain.model.RechangeModel
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

class RechangeRepositoryImpl (
    private val api: RechangeService,
    private val rechangeDao: RechangeDao
) : RechangeRepository {

    /**Dao*/
    override fun listAllDateRechangeFromDataBase(): Flow<List<RechangeModel>> {
        return rechangeDao.getAllDateRechange().map { items ->
            items.map { rechangeEntity ->
                RechangeMapper.toDomain(rechangeEntity)
            }
        }
    }

    override fun listForDate(data: String): Flow<List<RechangeModel>> {
        return rechangeDao.getListForDate(data).map { items ->
            items.map { rechangeEntity ->
                RechangeMapper.toDomain(rechangeEntity)
            }
        }
    }

    override suspend fun save(rechangeModel: RechangeModel) {
        rechangeDao.insert(RechangeMapper.toDatabase(rechangeModel))
    }

    override suspend fun delete(rechangeModel: RechangeModel) {
        return rechangeDao.delete(RechangeMapper.toDatabase(rechangeModel))
    }


    /**API*/

    override suspend fun listAllDateRechangeFromApi(): List<RechangeModel> {
        val response: List<RechangeEntity> = api.getAllRechanges()
        return response.map { rechangeEntity ->
            RechangeMapper.toDomain(rechangeEntity)
        }
    }

}