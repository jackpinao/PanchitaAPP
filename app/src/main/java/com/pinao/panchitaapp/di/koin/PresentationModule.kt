package com.pinao.panchitaapp.di.koin

import android.content.Context
import com.pinao.panchitaapp.domain.service.TicketPdfService
import com.pinao.panchitaapp.domain.usecase.category.CategoryUseCases
import com.pinao.panchitaapp.domain.usecase.category.SaveCategoryUseCase
import com.pinao.panchitaapp.domain.usecase.category.CheckCategoryNameUseCase
import com.pinao.panchitaapp.domain.usecase.client.SaveClientUseCase
import com.pinao.panchitaapp.domain.usecase.products.DeleteProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.FindCodeProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.GetAllProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.domain.usecase.products.RefreshProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.SaveProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.ScanBarcodeUseCase
import com.pinao.panchitaapp.domain.usecase.products.SearchProductsUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetAllDateRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.rechange.GetListForDateRechangeUC
import com.pinao.panchitaapp.domain.usecase.rechange.SaveRechangeUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.ClearTemporaryProductsUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.DeleteTemporaryProductUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.GetAllTemporaryProductsUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.SaveTemporaryProductUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.TemporaryProductUseCases
import com.pinao.panchitaapp.domain.usecase.ticket.CompleteSaleUseCase
import com.pinao.panchitaapp.domain.usecase.ticket.DetailTicketUseCases
import com.pinao.panchitaapp.presentation.ui.addCategory.AddCategoryViewModel
import com.pinao.panchitaapp.presentation.ui.addProduct.AddProductViewModel
import com.pinao.panchitaapp.presentation.ui.clarorecarga.ClaroRecargaViewModel
import com.pinao.panchitaapp.presentation.ui.guiaremision.GuiaRemisionViewModel
import com.pinao.panchitaapp.presentation.ui.guiaremision.search.ProductSearchViewModel
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
        deleteProductUseCase: DeleteProductUseCase,
        refreshProductsUseCase: RefreshProductsUseCase,
        searchProductsUseCase: SearchProductsUseCase
    ): ProductUseCases = ProductUseCases(
        getAll = getAllProductsUseCase,
        findByCode = findCodeProductUseCase,
        save = saveProductsUseCase,
        delete = deleteProductUseCase,
        refreshProducts = refreshProductsUseCase,
        search = searchProductsUseCase
    )

    @Factory
    fun provideTemporaryProductUseCases(
        getAllTemporaryProductsUseCase: GetAllTemporaryProductsUseCase,
        saveTemporaryProductUseCase: SaveTemporaryProductUseCase,
        deleteTemporaryProductUseCase: DeleteTemporaryProductUseCase,
        clearTemporaryProductsUseCase: ClearTemporaryProductsUseCase
    ): TemporaryProductUseCases = TemporaryProductUseCases(
        getAll = getAllTemporaryProductsUseCase,
        save = saveTemporaryProductUseCase,
        delete = deleteTemporaryProductUseCase,
        clearAll = clearTemporaryProductsUseCase
    )

    // ViewModel for Guia Remision
    @KoinViewModel
    fun provideGuiaRemisionViewModel(
        productUseCases: ProductUseCases,
        saveClientUseCase: SaveClientUseCase,
        scanBarcodeUseCase: ScanBarcodeUseCase,
        completeSaleUseCase: CompleteSaleUseCase,
        pdfService: TicketPdfService,
        temporaryProductUseCases: TemporaryProductUseCases
    ): GuiaRemisionViewModel =
        GuiaRemisionViewModel(
            productUseCases,
            saveClientUseCase,
            scanBarcodeUseCase,
            completeSaleUseCase,
            pdfService,
            temporaryProductUseCases
        )

    @KoinViewModel
    fun provideAddProductViewModel(
        productUseCases: ProductUseCases,
        categoryUseCases: CategoryUseCases,
        scanBarcodeUseCase: ScanBarcodeUseCase
    ): AddProductViewModel = AddProductViewModel(
        productUseCases,
        categoryUseCases,
        scanBarcodeUseCase
    )

    @KoinViewModel
    fun provideAddCategoryViewModel(
        saveCategoryUseCase: SaveCategoryUseCase,
        checkCategoryNameUseCase: CheckCategoryNameUseCase
    ): AddCategoryViewModel = AddCategoryViewModel(
        saveCategoryUseCase,
        checkCategoryNameUseCase
    )

    @KoinViewModel
    fun provideProductSearchViewModel(
        productUseCases: ProductUseCases
    ): ProductSearchViewModel = ProductSearchViewModel(productUseCases)
}