package com.pinao.panchitaapp.di.koin

import com.pinao.panchitaapp.data.repository.ProductsRepositoryImpl
import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.usecase.products.DeleteProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.FindCodeProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.GetAllProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.SaveProductsUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class DomainModule {
    @Factory
    fun provideGetAllDateRechangeUseCase(
        repositoryImpl: RechangeRepositoryImpl
    ) = GetAllDateRechangeUseCase(repositoryImpl)

    @Factory
    fun provideGetListForDateRechangeUC(
        repositoryImpl: RechangeRepositoryImpl
    ) = GetListForDateRechangeUC(repositoryImpl)

    @Factory
    fun provideSaveRechangeUseCase(
        repositoryImpl: RechangeRepositoryImpl
    ) = SaveRechangeUseCase(repositoryImpl)

    //add products use cases here if needed
    @Factory
    fun provideDeleteProductUseCase(
        repositoryImpl: ProductsRepositoryImpl
    ) = DeleteProductUseCase(repositoryImpl)

    @Factory
    fun provideGetAllProductsUseCase(
        repositoryImpl: ProductsRepositoryImpl
    ) = GetAllProductsUseCase(repositoryImpl)

    @Factory
    fun provideSaveProductUseCase(
        repositoryImpl: ProductsRepositoryImpl
    ) = SaveProductsUseCase(repositoryImpl)

    @Factory
    fun provideFindProductUseCase(
        repositoryImpl: ProductsRepositoryImpl
    ) = FindCodeProductUseCase(repositoryImpl)
}