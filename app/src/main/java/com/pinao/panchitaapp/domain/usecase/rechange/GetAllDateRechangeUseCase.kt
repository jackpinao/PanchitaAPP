package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.RechangeModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDateRechangeUseCase @Inject constructor(
    private val repository: RechangeRepositoryImpl
) {
     operator fun invoke() : Flow<List<RechangeModel>> {
        return repository.listAllDateRechangeFromDataBase()
    }
}