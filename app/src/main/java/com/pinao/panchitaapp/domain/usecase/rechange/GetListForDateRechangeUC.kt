package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.Rechange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListForDateRechangeUC @Inject constructor(
    private val repository: RechangeRepositoryImpl
) {
     operator fun invoke(date: String) : Flow<List<Rechange>> {
        return repository.listForDate(date)
    }
}