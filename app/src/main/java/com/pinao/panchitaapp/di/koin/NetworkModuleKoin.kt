package com.pinao.panchitaapp.di.koin

import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
//import org.koin.core.qualifier.named
// import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//val appModule = module {
//    single(named(Qualifier.ProvideRetrofit)) {
//        Retrofit.Builder()
//            .baseUrl("https://jsonplaceholder.typicode.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//    // Provide SaveRechangeUseCase
//    single { SaveRechangeUseCase(get()) }
//
//    // Provide ClaroRecargaViewModel
//    //viewModel { ClaroRecargaViewModel() }
//}
//
//enum class Qualifier {
//    ProvideRetrofit,
//    ProvideDatabase,
//    ProvideTicketDao,
//    ProvideProductDao,
//    ProvideDetailTicketDao,
//    ProvideUserDao,
//    ProvideClientDao,
//    ProvideUserRepository,
//    ProvideRechangeRepository,
//    ProvideSaveRechangeUseCase
//}
