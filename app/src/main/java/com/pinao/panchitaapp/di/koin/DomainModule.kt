package com.pinao.panchitaapp.di.koin

import com.pinao.panchitaapp.domain.repository.AuthRepository
import com.pinao.panchitaapp.domain.repository.BarcodeScanner
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import com.pinao.panchitaapp.domain.repository.ClientRepository
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import com.pinao.panchitaapp.domain.repository.ProductRepository
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import com.pinao.panchitaapp.domain.repository.TemporaryProductRepository
import com.pinao.panchitaapp.domain.repository.SaleRepository
import com.pinao.panchitaapp.domain.usecase.Auth.AuthUseCase
import com.pinao.panchitaapp.domain.usecase.Auth.IsUserLoggedInUseCase
import com.pinao.panchitaapp.domain.usecase.Auth.SignInUseCase
import com.pinao.panchitaapp.domain.usecase.Auth.SignOutUseCase
import com.pinao.panchitaapp.domain.usecase.category.CategoryUseCases
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
import com.pinao.panchitaapp.domain.usecase.ticket.CompleteSaleUseCase
import com.pinao.panchitaapp.domain.usecase.ticket.DetailTicketUseCases
import com.pinao.panchitaapp.domain.usecase.ticket.GetDetailsByTicketIdUseCase
import com.pinao.panchitaapp.domain.usecase.ticket.SaveDetailTicketUseCase
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

    @Factory
    fun provideScanBarcodeUseCase(
        barcodeScanner: BarcodeScanner
    ) = ScanBarcodeUseCase(barcodeScanner)

    @Factory
    fun provideSearchProductsUseCase(
        productRepository: ProductRepository
    ) = SearchProductsUseCase(productRepository)

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

    @Factory
    fun provideCategoryUseCases(
        getAllCategoriesUseCase: GetAllCategoriesUseCase,
        findCategoryUseCase: FindCategoryUseCase,
        saveCategoryUseCase: SaveCategoryUseCase,
        deleteCategoryUseCase: DeleteCategoryUseCase,
        refreshCategoriesUseCase: RefreshCategoriesUseCase
    ) = CategoryUseCases(
        getAll = getAllCategoriesUseCase,
        findByCode = findCategoryUseCase,
        save = saveCategoryUseCase,
        delete = deleteCategoryUseCase,
        refreshCategories = refreshCategoriesUseCase
    )

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

    @Factory
    fun provideDetailTicketUseCases(
        saveDetailTicketUseCase: SaveDetailTicketUseCase,
        getDetailsByTicketIdUseCase: GetDetailsByTicketIdUseCase
    ) = DetailTicketUseCases(
        save = saveDetailTicketUseCase,
        getDetailsByTicketId = getDetailsByTicketIdUseCase
    )

    @Factory
    fun provideSaveDetailTicketUseCase(
        detailTicketRepository: DetailTicketRepository
    ) = SaveDetailTicketUseCase(detailTicketRepository)

    @Factory
    fun provideGetDetailsByTicketIdUseCase(
        detailTicketRepository: DetailTicketRepository
    ) = GetDetailsByTicketIdUseCase(detailTicketRepository)

    @Factory
    fun provideRefrershProductsUseCase(
        productRepository: ProductRepository
    ) = RefreshProductsUseCase(productRepository)

    @Factory
    fun provideCompleteSaleUseCase(
        saleRepository: SaleRepository
    ) = CompleteSaleUseCase(saleRepository)

    @Factory
    fun provideGetAllTemporaryProductsUseCase(
        temporaryProductRepository: TemporaryProductRepository
    ) = GetAllTemporaryProductsUseCase(temporaryProductRepository)

    @Factory
    fun provideSaveTemporaryProductUseCase(
        temporaryProductRepository: TemporaryProductRepository
    ) = SaveTemporaryProductUseCase(temporaryProductRepository)

    @Factory
    fun provideDeleteTemporaryProductUseCase(
        temporaryProductRepository: TemporaryProductRepository
    ) = DeleteTemporaryProductUseCase(temporaryProductRepository)

    @Factory
    fun provideClearTemporaryProductsUseCase(
        temporaryProductRepository: TemporaryProductRepository
    ) = ClearTemporaryProductsUseCase(temporaryProductRepository)

    @Factory
    fun provideSignInUseCase(
        authRepository: AuthRepository
    ) = SignInUseCase(authRepository)

    @Factory
    fun provideIsUserLoggedInUseCase(
        authRepository: AuthRepository
    ) = IsUserLoggedInUseCase(authRepository)

    @Factory
    fun provideSignOutUseCase(
        authRepository: AuthRepository
    ) = SignOutUseCase(authRepository)

    @Factory
    fun provideAuthUseCase(
        signInUseCase: SignInUseCase,
        isUserLoggedInUseCase: IsUserLoggedInUseCase,
        signOutUseCase: SignOutUseCase
    ) = AuthUseCase(
        signInUseCase = signInUseCase,
        isUserLoggedInUseCase = isUserLoggedInUseCase,
        signOutUseCase = signOutUseCase
    )

}
