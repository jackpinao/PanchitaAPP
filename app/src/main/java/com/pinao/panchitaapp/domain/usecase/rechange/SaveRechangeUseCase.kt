package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.RechangeModel
import javax.inject.Inject

class SaveRechangeUseCase @Inject constructor(
    private val repository: RechangeRepositoryImpl
) {
    suspend operator fun invoke(rechangeModel: RechangeModel) {
        repository.save(rechangeModel)
    }
}


