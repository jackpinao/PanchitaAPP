package com.pinao.panchitaapp.di

import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    //viewModel { ClaroRecargaViewModel(get()) }
//    viewModelOf(::ClaroRecargaViewModel)
//    viewModelOf(::HomeViewModel)
//    viewModelOf(::LoginViewModel)
    viewModel { ClaroRecargaViewModel(get()) }
}