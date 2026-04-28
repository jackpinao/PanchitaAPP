package com.pinao.panchitaapp.di.koin

import com.pinao.panchitaapp.domain.usecase.products.DeleteProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.FindCodeProductUseCase
import com.pinao.panchitaapp.domain.usecase.products.GetAllProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.domain.usecase.products.RefreshProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.SaveProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.SaveStockEntryUseCase
import com.pinao.panchitaapp.domain.usecase.products.SearchProductsUseCase
import com.pinao.panchitaapp.domain.usecase.products.SyncUnsyncedProductsUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.ClearTemporaryProductsUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.DeleteTemporaryProductUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.GetAllTemporaryProductsUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.SaveTemporaryProductUseCase
import com.pinao.panchitaapp.domain.usecase.temporary.TemporaryProductUseCases
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.pinao.panchitaapp.presentation.ui")
class PresentationModule {

    @Factory
    fun provideProductUseCases(
        getAllProductsUseCase: GetAllProductsUseCase,
        findCodeProductUseCase: FindCodeProductUseCase,
        saveProductsUseCase: SaveProductsUseCase,
        deleteProductUseCase: DeleteProductUseCase,
        refreshProductsUseCase: RefreshProductsUseCase,
        searchProductsUseCase: SearchProductsUseCase,
        syncUnsyncedProductsUseCase: SyncUnsyncedProductsUseCase,
        saveStockEntryUseCase: SaveStockEntryUseCase
    ): ProductUseCases = ProductUseCases(
        getAll = getAllProductsUseCase,
        findByCode = findCodeProductUseCase,
        save = saveProductsUseCase,
        delete = deleteProductUseCase,
        refreshProducts = refreshProductsUseCase,
        search = searchProductsUseCase,
        syncUnsyncedProducts = syncUnsyncedProductsUseCase,
        saveStockEntry = saveStockEntryUseCase
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
}
