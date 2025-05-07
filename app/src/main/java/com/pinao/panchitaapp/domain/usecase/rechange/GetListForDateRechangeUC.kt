package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.RechangeModel
import kotlinx.coroutines.flow.Flow

class GetListForDateRechangeUC(
    private val repository: RechangeRepositoryImpl
) {
    operator fun invoke(date: String): Flow<List<RechangeModel>> {
        return repository.listForDate(date)
    }
}