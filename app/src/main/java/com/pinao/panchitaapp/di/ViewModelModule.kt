package com.pinao.panchitaapp.di

import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ClaroRecargaViewModel(get<SaveRechangeUseCase>()) }
    viewModelOf(::ClaroRecargaViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
}