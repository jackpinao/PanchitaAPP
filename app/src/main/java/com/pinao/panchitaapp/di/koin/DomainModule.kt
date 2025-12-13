package com.pinao.panchitaapp.di.koin

import com.pinao.panchitaapp.domain.repository.CategoryRepository
import com.pinao.panchitaapp.domain.repository.ClientRepository
import com.pinao.panchitaapp.domain.repository.ProductRepository
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import com.pinao.panchitaapp.domain.usecase.category.CheckCategoryNameUseCase
import com.pinao.panchitaapp.domain.usecase.category.DeleteCategoryUseCase
import com.pinao.panchitaapp.domain.usecase.category.FindCategoryUseCase
import com.pinao.panchitaapp.domain.usecase.category.GetAllCategoriesUseCase
import com.pinao.panchitaapp.domain.usecase.category.RefreshCategoriesUseCase
import com.pinao.panchitaapp.domain.usecase.category.SaveCategoryUseCase
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

@Module
class DomainModule {
    /*
    RECHANGE USE CASES
    */
    @Factory
    fun provideGetAllDateRechangeUseCase(
        rechangeRepository: RechangeRepository
    ) = GetAllDateRechangeUseCase(rechangeRepository)

    @Factory
    fun provideGetListForDateRechangeUC(
        rechangeRepository: RechangeRepository
    ) = GetListForDateRechangeUC(rechangeRepository)

    @Factory
    fun provideSaveRechangeUseCase(
        rechangeRepository: RechangeRepository
    ) = SaveRechangeUseCase(rechangeRepository)

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
    CATEGORY USE CASES
     */
    @Factory
    fun provideCheckCategoryNameUseCase(
        categoryRepository: CategoryRepository
    ) = CheckCategoryNameUseCase(categoryRepository)

    @Factory
    fun provideSaveCategoryUseCase(
        categoryRepository: CategoryRepository
    ) = SaveCategoryUseCase(categoryRepository)

    @Factory
    fun provideDeleteCategoryUseCase(
        categoryRepository: CategoryRepository
    ) = DeleteCategoryUseCase(categoryRepository)

    @Factory
    fun provideGetAllCategoriesUseCase(
        categoryRepository: CategoryRepository
    ) = GetAllCategoriesUseCase(categoryRepository)

    @Factory
    fun provideFindCategoryUseCase(
        categoryRepository: CategoryRepository
    ) = FindCategoryUseCase(categoryRepository)

    @Factory
    fun provideRefreshCategoriesUseCase(
        categoryRepository: CategoryRepository
    ) = RefreshCategoriesUseCase(categoryRepository)


    /*
    CLIENTS USE CASES
     */
    @Factory
    fun provideGetAllClientsUseCase(
        clientRepository: ClientRepository
    ) = GetAllClientsUseCase(clientRepository)

    @Factory
    fun provideSaveClientUseCase(
        clientRepository: ClientRepository
    ) = SaveClientUseCase(clientRepository)

    @Factory
    fun provideDeleteClientUseCase(
        clientRepository: ClientRepository
    ) = DeleteClientUseCase(clientRepository)

    @Factory
    fun provideFindClientUseCase(
        clientRepository: ClientRepository
    ) = FindClientUseCase(clientRepository)
}