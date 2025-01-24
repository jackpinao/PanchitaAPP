package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.Rechange

class SaveRechangeUseCase(
    private val repository: RechangeRepositoryImpl
) {
    suspend operator fun invoke(rechange: Rechange) {
        repository.save(rechange)
    }
}


