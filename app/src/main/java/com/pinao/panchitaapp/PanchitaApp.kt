package com.pinao.panchitaapp

import android.app.Application
import com.pinao.panchitaapp.di.appModule
import com.pinao.panchitaapp.di.dataModule
import com.pinao.panchitaapp.di.repositoryModule
import com.pinao.panchitaapp.di.useCaseModule
import com.pinao.panchitaapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PanchitaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        //MobileAds.initialize(this)
        startKoin{
            androidLogger(Level.DEBUG)
            androidContext(this@PanchitaApp)
            modules(appModule, dataModule, viewModelModule, useCaseModule, repositoryModule)
        }
    }
}