package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.domain.model.RechangeModel
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import kotlinx.coroutines.flow.Flow

class GetAllDateRechangeUseCase(
    private val repository: RechangeRepository
) {
    operator fun invoke(): Flow<List<RechangeModel>> {
        return repository.listAllDateRechangeFromDataBase()
    }
}