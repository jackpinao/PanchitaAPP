package com.pinao.panchitaapp.test.viewModel

import com.pinao.panchitaapp.domain.usecase.brand.BrandUseCases
import com.pinao.panchitaapp.domain.usecase.category.CategoryUseCases
import com.pinao.panchitaapp.domain.usecase.category.GetAllCategoriesUseCase
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.domain.usecase.products.ScanBarcodeUseCase
import com.pinao.panchitaapp.presentation.ui.addProduct.AddProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class AddProductViewModelTest {

    private lateinit var productUseCases: ProductUseCases
    private lateinit var categoryUseCases: CategoryUseCases
    private lateinit var scanBarcodeUseCase: ScanBarcodeUseCase
    private lateinit var getAllCategoriesUseCase: GetAllCategoriesUseCase
    private lateinit var brandUseCases: BrandUseCases

    private lateinit var viewModel: AddProductViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        productUseCases = mock(ProductUseCases::class.java)
        categoryUseCases = mock(CategoryUseCases::class.java)
        scanBarcodeUseCase = mock(ScanBarcodeUseCase::class.java)
        getAllCategoriesUseCase = mock(GetAllCategoriesUseCase::class.java)
        brandUseCases = mock(BrandUseCases::class.java)

        `when`(categoryUseCases.getAll).thenReturn(getAllCategoriesUseCase)

        // Mock por defecto para el init del ViewModel
        `when`(getAllCategoriesUseCase()).thenReturn(flowOf(emptyList()))

        viewModel = AddProductViewModel(
            productUseCases = productUseCases,
            categoryUseCases = categoryUseCases,
            scanBarcodeUseCase = scanBarcodeUseCase,
            brandUseCases = brandUseCases
        )
    }

    @Test
    fun `onPriceChange validates numeric input correctly`() {
        viewModel.onPriceChange("12.50")
        assertEquals("12.50", viewModel.uiState.value.productPurchasePrice)

        viewModel.onPriceChange("12.50a") // Letra inválida
        assertEquals("12.50", viewModel.uiState.value.productPurchasePrice)
    }
}
