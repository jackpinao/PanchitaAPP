package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.domain.repository.RechangeRepository

class SaveRechangeUseCase(
    private val repository: RechangeRepository
) {
    suspend operator fun invoke(rechange: Rechange) {
        repository.save(rechange)
    }
}


