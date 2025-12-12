package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.domain.model.RechangeModel
import com.pinao.panchitaapp.domain.repository.RechangeRepository

class SaveRechangeUseCase(
    private val repository: RechangeRepository
) {
    suspend operator fun invoke(rechangeModel: RechangeModel) {
        repository.save(rechangeModel)
    }
}


