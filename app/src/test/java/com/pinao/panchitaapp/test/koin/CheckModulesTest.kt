package com.pinao.panchitaapp.test.koin

import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.dao.UserDao
import com.pinao.panchitaapp.di.appModule
import com.pinao.panchitaapp.di.dataModule
import com.pinao.panchitaapp.di.repositoryModule
import com.pinao.panchitaapp.di.useCaseModule
import com.pinao.panchitaapp.di.viewModelModule
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckModulesTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `check module appModule`() {
        appModule.verify()
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `check module repositoryModule`() {
        repositoryModule.verify(
            extraTypes =
            listOf(RechangeDao::class, UserDao::class)
        )
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `check module roomModule`() {
        dataModule.verify()
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `check module useCaseModule`() {
        useCaseModule.verify(
            extraTypes = listOf(RechangeRepository::class)
        )
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `check module viewModelModule`() {
        viewModelModule.verify(
            extraTypes = listOf(SaveRechangeUseCase::class)
        )
    }
}