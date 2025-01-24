package com.pinao.panchitaapp.di

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {

    single<SaveRechangeUseCase>(named(Qualifier.ProvideSaveRechangeUseCase)) {
        SaveRechangeUseCase(
            get<RechangeRepositoryImpl>()
        )
    }
    factoryOf(::SaveRechangeUseCase)

}