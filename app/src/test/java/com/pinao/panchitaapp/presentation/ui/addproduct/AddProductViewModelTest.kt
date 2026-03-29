package com.pinao.panchitaapp.presentation.ui.addproduct

import app.cash.turbine.test
import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.usecase.brand.BrandUseCases
import com.pinao.panchitaapp.domain.usecase.category.CategoryUseCases
import com.pinao.panchitaapp.domain.usecase.products.ProductUseCases
import com.pinao.panchitaapp.domain.usecase.products.ScanBarcodeUseCase
import com.pinao.panchitaapp.presentation.ui.addProduct.AddProductViewModel
import com.pinao.panchitaapp.test.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val productUseCases: ProductUseCases = mockk(relaxed = true)
    private val categoryUseCases: CategoryUseCases = mockk(relaxed = true)
    private val scanBarcodeUseCase: ScanBarcodeUseCase = mockk()
    private val brandUseCases: BrandUseCases = mockk(relaxed = true)

    private lateinit var viewModel: AddProductViewModel

    private val sampleCategories = listOf(
        CategoryModel("cat1", name = "Bebidas", revenue = 10.0),
        CategoryModel("cat2", name = "Snacks", revenue = 15.0)
    )

    private val sampleBrands = listOf(
        BrandModel("brand1", name = "Coca-Cola"),
        BrandModel("brand2", name = "Inka Cola")
    )

    @Before
    fun setup() {
        // Mock default behavior para los inits
        every { categoryUseCases.getAll() } returns flowOf(sampleCategories)
        coEvery { categoryUseCases.refreshCategories() } returns Unit
        every { brandUseCases.getAll() } returns flowOf(sampleBrands)
    }

    @Test
    fun `init should fetch categories and brands`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        // Advance time ANTES de suscribirse para no lidiar con emisiones intermedias
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        // Verificamos que al crearse el VM, cargue los valores desde la Base de Datos o API
        viewModel.uiState.test {
            val finalState = awaitItem()

            Assert.assertEquals(2, finalState.listOfCategoriesName.size)
            Assert.assertTrue(finalState.listOfCategoriesName.contains("Bebidas"))

            Assert.assertEquals(2, finalState.listOfBrandsName.size)
            Assert.assertTrue(finalState.listOfBrandsName.contains("Coca-Cola"))
        }

        coVerify { categoryUseCases.refreshCategories() }
    }

    @Test
    fun `onNameChange should update product name in state`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        viewModel.uiState.test {
            val initialState = awaitItem() // Inicial

            viewModel.onNameChange("Galletas")
            val newState = awaitItem()

            Assert.assertEquals("Galletas", newState.productName)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPriceChange should update price only if valid numbers`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        viewModel.uiState.test {
            val initialState = awaitItem() // Estado inicial

            viewModel.onPriceChange("12.5")
            val state1 = awaitItem()
            Assert.assertEquals("12.5", state1.productPurchasePrice)

            // Letras no válidas
            viewModel.onPriceChange("abc")
            // No emitirá un nuevo estado porque falla el Regex,
            // Turbine esperaría infinitamente un nuevo item
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCategoryChange should map category name to id and revenue`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        // Dejamos que las categorías carguen
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val loadedState = awaitItem()
            Assert.assertTrue(loadedState.listOfCategoriesName.isNotEmpty())

            // Act
            viewModel.onCategoryChange("Bebidas")
            val selectedState = awaitItem()

            // Assert
            Assert.assertEquals("Bebidas", selectedState.productCategory)
            Assert.assertEquals("cat1", selectedState.productCategoryId)
            Assert.assertEquals(10.0, selectedState.productRevenueCategory, 0.0)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadProduct with valid barcode should update state as EditMode`() = runTest {
        val mockProduct = ProductModel(
            productId = "prod1",
            barcode = "775123",
            name = "Inca Kola 500ml",
            categoryId = "cat1",
            brandId = "brand2",
            priceBuy = 1.5,
            priceSell = 2.0,
            priceExcludingIGV = 1.69,
            stockQuantity = 50.0
        )

        every { productUseCases.findByCode("775123") } returns flowOf(mockProduct)

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        // Avanzamos para que el init termine y limpie sus emisiones
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val stateAfterInit = awaitItem() // Estado estable

            viewModel.loadProduct("775123")

            // Loading state due to loadProduct
            val loadingState = awaitItem()
            Assert.assertTrue(loadingState.isLoading)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            Assert.assertFalse(loadedState.isLoading)
            Assert.assertTrue(loadedState.isEditMode)
            Assert.assertEquals("Inca Kola 500ml", loadedState.productName)
            Assert.assertEquals("1.5", loadedState.productPurchasePrice)
            Assert.assertEquals("50.0", loadedState.productStock)
            Assert.assertEquals("cat1", loadedState.productCategoryId)
            Assert.assertEquals("brand2", loadedState.productBrandId)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProduct with missing fields should set error`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        // Avanzamos el init
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem()

            // Act
            viewModel.saveProduct()

            val errorState = awaitItem()
            Assert.assertEquals("Por favor, completa los campos obligatorios", errorState.error)

            coVerify(exactly = 0) { productUseCases.save(any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startScanning should update productCode when scanned`() = runTest {
        coEvery { scanBarcodeUseCase() } returns "123456789"

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        // Avanzamos el init
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem()

            viewModel.startScanning()

            val loadingState = awaitItem()
            Assert.assertTrue(loadingState.isLoading)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            val resultState = awaitItem()

            Assert.assertEquals("123456789", resultState.productCode)
            Assert.assertFalse(resultState.isLoading)

            coVerify { scanBarcodeUseCase() }

            cancelAndIgnoreRemainingEvents()
        }
    }
}