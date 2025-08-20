package com.pinao.panchitaapp

import android.app.Application
import com.pinao.panchitaapp.di.koin.AppModule
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class PanchitaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            //androidLogger()
            androidContext(this@PanchitaApp)
            analytics()
            modules(
                AppModule().module
            )
        }
    }
}