package com.pinao.panchitaapp.presentation.ui.addproduct

import android.util.Log
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
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
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
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Mock default behavior para los inits
        every { categoryUseCases.getAll() } returns flowOf(sampleCategories)
        coEvery { categoryUseCases.refreshCategories() } returns Unit
        every { brandUseCases.getAll() } returns flowOf(sampleBrands)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `init should fetch categories and brands`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

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
    fun `listCategories error should be caught and set error state`() = runTest {
        every { categoryUseCases.getAll() } returns flow { throw Exception("DB Error") }

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val finalState = awaitItem()
            Assert.assertEquals("Error: DB Error", finalState.error)
            Assert.assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `onNameChange should update product name in state`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        viewModel.uiState.test {
            awaitItem()
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
            awaitItem()
            viewModel.onPriceChange("12.5")
            val state1 = awaitItem()
            Assert.assertEquals("12.5", state1.productTotalCost)
            viewModel.onPriceChange("abc")
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onStockChange should update stock only if valid numbers`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.onStockChange("10")
            val state1 = awaitItem()
            Assert.assertEquals("10", state1.productStock)
            viewModel.onStockChange("xyz")
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCodeChanged should update productCode`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.onCodeChanged("111222")
            val state = awaitItem()
            Assert.assertEquals("111222", state.productCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCategoryChange should map category name to id and revenue`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val loadedState = awaitItem()
            Assert.assertTrue(loadedState.listOfCategoriesName.isNotEmpty())
            viewModel.onCategoryChange("Bebidas")
            val selectedState = awaitItem()
            Assert.assertEquals("Bebidas", selectedState.productCategory)
            Assert.assertEquals("cat1", selectedState.productCategoryId)
            Assert.assertEquals(10.0, selectedState.productRevenueCategory, 0.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBrandChange should map brand name to id`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onBrandChange("Coca-Cola")
            val selectedState = awaitItem()
            Assert.assertEquals("Coca-Cola", selectedState.productBrand)
            Assert.assertEquals("brand1", selectedState.productBrandId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadProduct with valid barcode should update state as EditMode`() = runTest {
        val mockProduct = ProductModel(
            productId = "prod1", barcode = "775123", name = "Inca Kola 500ml",
            categoryId = "cat1", brandId = "brand2", priceBuy = 1.5, priceSell = 2.0,
            priceExcludingIGV = 1.69, stockQuantity = 50.0
        )
        every { productUseCases.findByCode("775123") } returns flowOf(mockProduct)

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.loadProduct("775123")

            val loadingState = awaitItem()
            Assert.assertTrue(loadingState.isLoading)
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            Assert.assertFalse(loadedState.isLoading)
            Assert.assertTrue(loadedState.isEditMode)
            Assert.assertEquals("Inca Kola 500ml", loadedState.productName)
            Assert.assertEquals("brand2", loadedState.productBrandId)
            Assert.assertEquals("cat1", loadedState.productCategoryId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadProduct with unknown barcode should stop loading and set code`() = runTest {
        every { productUseCases.findByCode("999") } returns flowOf(null)

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.loadProduct("999")

            Assert.assertTrue(awaitItem().isLoading) // Loading
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            Assert.assertFalse(loadedState.isLoading)
            Assert.assertFalse(loadedState.isEditMode)
            Assert.assertEquals("999", loadedState.productCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startScanning should update productCode when scanned`() = runTest {
        coEvery { scanBarcodeUseCase() } returns "123456789"
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.startScanning()

            Assert.assertTrue(awaitItem().isLoading) // Loading
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val resultState = awaitItem()
            Assert.assertEquals("123456789", resultState.productCode)
            Assert.assertFalse(resultState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startScanning should stop loading if scanner returns null`() = runTest {
        coEvery { scanBarcodeUseCase() } returns null
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.startScanning()

            Assert.assertTrue(awaitItem().isLoading) // Loading
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val resultState = awaitItem()
            Assert.assertFalse(resultState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProduct with missing fields should set error`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.saveProduct()
            val errorState = awaitItem()
            Assert.assertEquals("Por favor, completa los campos obligatorios", errorState.error)
            coVerify(exactly = 0) { productUseCases.save(any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProduct success should save to usecase and navigate back`() = runTest {
        coEvery { productUseCases.save(any()) } returns Unit

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            // Completamos los campos requeridos
            viewModel.onNameChange("Producto Test")
            viewModel.onPriceChange("10.0")
            viewModel.onCodeChanged("111")
            viewModel.onCategoryChange("Bebidas") // cat1, 10.0 revenue
            viewModel.onBrandChange("Coca-Cola") // brand1
            viewModel.onStockChange("5")

            // Consumimos todas las emisiones
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            val readyState = expectMostRecentItem()
            Assert.assertEquals("Producto Test", readyState.productName)

            // Act
            viewModel.saveProduct()

            // Assert
            val loadingState = awaitItem()
            Assert.assertTrue(loadingState.isLoading)

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            val successState = expectMostRecentItem()
            Assert.assertFalse(successState.isLoading)
            Assert.assertTrue(successState.navigateBack)

            coVerify {
                productUseCases.save(match {
                    it.name == "Producto Test" && it.categoryId == "cat1" && it.brandId == "brand1"
                })
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProduct with blank brandId should create General brand if empty list`() = runTest {
        coEvery { productUseCases.save(any()) } returns Unit
        coEvery { brandUseCases.save(any()) } returns Unit

        // Simulamos una lista de marcas Vacia
        every { brandUseCases.getAll() } returns flowOf(emptyList())

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onNameChange("Producto Test")
            viewModel.onPriceChange("10.0")
            viewModel.onStockChange("5.0")
            viewModel.onCodeChanged("111")
            viewModel.onCategoryChange("Bebidas")

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            viewModel.saveProduct() // El brandId está en blanco y la lista está vacía
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 1) { brandUseCases.save(match { it.name == "General" }) }
            coVerify { productUseCases.save(any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProduct exception should update error state`() = runTest {
        coEvery { productUseCases.save(any()) } throws Exception("Save Error")

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.onNameChange("Producto Test")
            viewModel.onPriceChange("10.0")
            viewModel.onStockChange("5.0")
            viewModel.onCodeChanged("111")
            viewModel.onCategoryChange("Bebidas")

            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem()

            viewModel.saveProduct()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val errorState = expectMostRecentItem()
            Assert.assertEquals("Error al guardar: Save Error", errorState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onErrorShow should clear error message`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.saveProduct() // Genera error por campos vacios

            val errorState = awaitItem()
            Assert.assertNotNull(errorState.error)

            viewModel.onErrorShow()
            val clearedState = awaitItem()
            Assert.assertNull(clearedState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProductInfo should not change existing stock or priceBuy`() = runTest {
        coEvery { productUseCases.save(any()) } returns Unit

        val mockProduct = ProductModel(
            productId = "prod1", barcode = "775123", name = "Original Name",
            categoryId = "cat1", brandId = "brand1", priceBuy = 3.0, priceSell = 5.0,
            priceExcludingIGV = 4.24, stockQuantity = 20.0
        )
        every { productUseCases.findByCode("775123") } returns flowOf(mockProduct)

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.loadProduct("775123")
            Assert.assertTrue(awaitItem().isLoading)
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            Assert.assertTrue(loadedState.isEditMode)

            viewModel.onNameChange("NUEVO NOMBRE")
            awaitItem()

            viewModel.saveProductInfo()
            Assert.assertTrue(awaitItem().isLoading)
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val saved = expectMostRecentItem()
            Assert.assertTrue(saved.navigateBack)

            coVerify {
                productUseCases.save(match { product ->
                    product.name == "NUEVO NOMBRE" &&
                    product.stockQuantity == 20.0 &&
                    product.priceBuy == 3.0
                })
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `registerStockEntry should recalculate PPP and save StockEntry`() = runTest {
        coEvery { productUseCases.save(any()) } returns Unit
        coEvery { productUseCases.saveStockEntry(any()) } returns Unit

        val mockProduct = ProductModel(
            productId = "prod1", barcode = "775123", name = "Inca Kola",
            categoryId = "cat1", brandId = "brand1", priceBuy = 2.0, priceSell = 3.0,
            priceExcludingIGV = 2.54, stockQuantity = 10.0
        )
        every { productUseCases.findByCode("775123") } returns flowOf(mockProduct)

        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.loadProduct("775123")
            Assert.assertTrue(awaitItem().isLoading)
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // loaded state

            // PPP: (10*2.0 + 10.0) / (10 + 5) = 30/15 = 2.0
            viewModel.onPriceChange("10.0")  // totalCost para 5 unidades
            awaitItem()
            viewModel.onStockChange("5")
            awaitItem()

            viewModel.registerStockEntry()
            Assert.assertTrue(awaitItem().isLoading)
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

            val successState = expectMostRecentItem()
            Assert.assertTrue(successState.navigateBack)

            coVerify { productUseCases.save(match { it.stockQuantity == 15.0 }) }
            coVerify { productUseCases.saveStockEntry(match { entry ->
                entry.productId == "prod1" &&
                entry.quantityAdded == 5.0 &&
                entry.movementType == "ENTRY"
            }) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `registerStockEntry with zero stock should show error and not save`() = runTest {
        viewModel = AddProductViewModel(
            productUseCases, categoryUseCases, scanBarcodeUseCase, brandUseCases
        )
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.registerStockEntry()
            val errorState = awaitItem()
            Assert.assertEquals("La cantidad de stock a añadir debe ser mayor a 0", errorState.error)
            coVerify(exactly = 0) { productUseCases.save(any()) }
            coVerify(exactly = 0) { productUseCases.saveStockEntry(any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }
}