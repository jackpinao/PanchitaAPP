package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import javax.inject.Inject

class GetRechangeUseCase @Inject constructor(
    private val repository: RechangeRepositoryImpl
) {
    suspend operator fun invoke(data: String) {
        repository.listDate(data)
    }
}