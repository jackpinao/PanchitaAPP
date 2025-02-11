package com.pinao.panchitaapp.domain.usecase.rechange

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.model.Rechange
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import javax.inject.Inject
import javax.inject.Singleton

class SaveRechangeUseCase @Inject constructor(
    private val repository: RechangeRepositoryImpl
) {
    suspend operator fun invoke(rechange: Rechange) {
        repository.save(rechange)
    }
}


