package com.pinao.panchitaapp.di

import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.data.repository.UserRepositoryImpl
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import com.pinao.panchitaapp.domain.repository.UserRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository>(named(Qualifier.ProvideUserRepository)) {
        UserRepositoryImpl(get())
    }
    single<RechangeRepository>(named(Qualifier.ProvideRechangeRepository)) {
        RechangeRepositoryImpl(get())
    }
    factoryOf(::UserRepositoryImpl)
    factoryOf(::RechangeRepositoryImpl)
}