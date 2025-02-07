package com.pinao.panchitaapp.di

import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.core.module.dsl.*

val appModule = module {
    single(named(Qualifier.ProvideRetrofit)) {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    // Provide SaveRechangeUseCase
    single { SaveRechangeUseCase(get()) }

    // Provide ClaroRecargaViewModel
    //viewModel { ClaroRecargaViewModel() }
}

enum class Qualifier {
    ProvideRetrofit,
    ProvideDatabase,
    ProvideTicketDao,
    ProvideProductDao,
    ProvideDetailTicketDao,
    ProvideUserDao,
    ProvideClientDao,
    ProvideUserRepository,
    ProvideRechangeRepository,
    ProvideSaveRechangeUseCase
}
