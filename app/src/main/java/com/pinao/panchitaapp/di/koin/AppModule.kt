package com.pinao.panchitaapp.di.koin

import org.koin.core.annotation.Module

@Module(includes = [DataModule::class, DomainModule::class, PresentationModule::class])
class AppModule