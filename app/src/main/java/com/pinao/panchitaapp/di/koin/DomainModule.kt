package com.pinao.panchitaapp.di.koin

import com.pinao.panchitaapp.data.repository.ClientRepositoryImpl
import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.repository.ClientRepository
import com.pinao.panchitaapp.domain.repository.ProductRepository
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import com.pinao.panchitaapp.domain.usecase.client.DeleteClientUseCase
import com.pinao.panchitaapp.domain.usecase.client.FindClientUseCase
import com.pinao.panchitaapp.domain.usecase.client.GetAllClientsUseCase
import com.pinao.panchitaapp.domain.usecase.client.SaveClientUseCase
import com.pinao.panchitaapp.domain.usecase.products.DeleteProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.FindCodeProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.GetAllProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.SaveProductsUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DomainModule {
    /*
    RECHANGE USE CASES
    */
    //@Factory
    @Single
    fun provideGetAllDateRechangeUseCase(
        repository: RechangeRepository
    ) = GetAllDateRechangeUseCase(repository)

    @Factory
    fun provideGetListForDateRechangeUC(
        repository: RechangeRepository
    ) = GetListForDateRechangeUC(repository)

    @Factory
    fun provideSaveRechangeUseCase(
        repository: RechangeRepository
    ) = SaveRechangeUseCase(repository)

    /*
    PRODUCTS USE CASES
     */
    @Factory
    fun provideDeleteProductUseCase(
        productRepository: ProductRepository
    ) = DeleteProductUseCase(productRepository)

    @Factory
    fun provideGetAllProductsUseCase(
        productRepository: ProductRepository
    ) = GetAllProductsUseCase(productRepository)

    @Factory
    fun provideSaveProductUseCase(
        productRepository: ProductRepository
    ) = SaveProductsUseCase(productRepository)

    @Factory
    fun provideFindProductUseCase(
        productRepository: ProductRepository
    ) = FindCodeProductUseCase(productRepository)
    /*
    CLIENTS USE CASES
     */
    @Factory
    fun provideGetAllClientsUseCase(
        repository: ClientRepository
    ) = GetAllClientsUseCase(repository)
    @Factory
    fun provideSaveClientUseCase(
        repository: ClientRepository
    ) = SaveClientUseCase(repository)
    @Factory
    fun provideDeleteClientUseCase(
        repository: ClientRepository
    ) = DeleteClientUseCase(repository)
    @Factory
    fun provideFindClientUseCase(
        repository: ClientRepository
    ) = FindClientUseCase(repository)

}