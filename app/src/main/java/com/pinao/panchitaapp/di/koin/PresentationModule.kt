package com.pinao.panchitaapp.di.koin

import android.os.Build
import androidx.annotation.RequiresApi
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class PresentationModule {

    //@RequiresApi(Build.VERSION_CODES.O)
    @KoinViewModel
    fun provideClaroRecargaViewModel(
        saveRechangeUseCase: SaveRechangeUseCase,
        getListForDateRechangeUC: GetListForDateRechangeUC,
        getAllDateRechangeUseCase: GetAllDateRechangeUseCase
    ): ClaroRecargaViewModel =
        ClaroRecargaViewModel(
            saveRechangeUseCase,
            getListForDateRechangeUC,
            getAllDateRechangeUseCase
        )

    @KoinViewModel
    fun provideHomeViewModel(): HomeViewModel = HomeViewModel()

    @KoinViewModel
    fun provideLoginViewModel(): LoginViewModel = LoginViewModel()
}