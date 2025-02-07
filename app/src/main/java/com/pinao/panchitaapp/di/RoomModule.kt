package com.pinao.panchitaapp.di

import android.content.Context
import androidx.room.Room
import com.pinao.panchitaapp.data.local.database.AppDatabase
import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.data.repository.UserRepositoryImpl
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import com.pinao.panchitaapp.domain.repository.UserRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    val DATABASE_NAME = "panchita_app"
    single(named(Qualifier.ProvideDatabase)) {
        Room.databaseBuilder(
            get<Context>(),
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
    single(named(Qualifier.ProvideTicketDao)) {
        get<AppDatabase>().ticketDao()
    }
    single(named(Qualifier.ProvideProductDao)) {
        get<AppDatabase>().productDao()
    }
    single(named(Qualifier.ProvideDetailTicketDao)) {
        get<AppDatabase>().detailTicketDao()
    }
    single(named(Qualifier.ProvideUserDao)) {
        get<AppDatabase>().userDao()
    }
    single(named(Qualifier.ProvideClientDao)) {
        get<AppDatabase>().clientDao()
    }
    single<UserRepository>(named(Qualifier.ProvideUserRepository)) {
        UserRepositoryImpl(get())
    }
    single<RechangeRepository>(named(Qualifier.ProvideRechangeRepository)) {
        RechangeRepositoryImpl(get())
    }
    factoryOf(::UserRepositoryImpl)
    factoryOf(::RechangeRepositoryImpl)
}