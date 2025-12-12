package com.pinao.panchitaapp.di.koin

import android.content.Context
import com.pinao.panchitaapp.domain.usecase.client.SaveClientUseCase
import com.pinao.panchitaapp.domain.usecase.products.DeleteProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.FindCodeProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.GetAllProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.domain.usecase.products.SaveProductsUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.presentation.ui.addProduct.AddProductViewModel
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import com.pinao.panchitaapp.presentation.ui.guiaremision.GuiaRemisionViewModel
import com.pinao.panchitaapp.presentation.ui.home.HomeViewModel
import com.pinao.panchitaapp.presentation.ui.login.LoginViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class PresentationModule {

    // ViewModel for Claro Recarga
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

    @Factory
    fun provideProductUseCases(
        getAllProductsUseCase: GetAllProductsUseCase,
        findCodeProductUseCase: FindCodeProductUseCase,
        saveProductsUseCase: SaveProductsUseCase,
        deleteProductUseCase: DeleteProductUseCase
    ): ProductUseCases = ProductUseCases(
        getAll = getAllProductsUseCase,
        findByCode = findCodeProductUseCase,
        save = saveProductsUseCase,
        delete = deleteProductUseCase
    )

    // ViewModel for Guia Remision
    @KoinViewModel
    fun provideGuiaRemisionViewModel(
        context: Context,
        productUseCases: ProductUseCases,
        saveClientUseCase: SaveClientUseCase
    ): GuiaRemisionViewModel =
        GuiaRemisionViewModel(
            context,
            productUseCases,
            saveClientUseCase
        )

    @KoinViewModel
    fun provideAddProductViewModel(): AddProductViewModel = AddProductViewModel()

}