package com.pinao.panchitaapp.data.repository

import android.util.Log
import app.cash.turbine.test
import com.pinao.panchitaapp.data.source.local.dao.BrandDao
import com.pinao.panchitaapp.data.source.local.dao.CategoryDao
import com.pinao.panchitaapp.data.source.local.dao.ProductDao
import com.pinao.panchitaapp.data.source.local.entity.ProductsEntity
import com.pinao.panchitaapp.data.source.remote.RemoteDataSource
import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.model.ProductModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsRepositoryImplTest {

    private lateinit var repository: ProductsRepositoryImpl
    private val mockProductDao: ProductDao = mockk(relaxed = true)
    private val mockCategoryDao: CategoryDao = mockk(relaxed = true)
    private val mockBrandDao: BrandDao = mockk(relaxed = true)
    private val mockRemoteDataSource: RemoteDataSource = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        repository = ProductsRepositoryImpl(
            mockProductDao,
            mockCategoryDao, 
            mockBrandDao,
            mockRemoteDataSource
        )
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `getAllProductsFromDataBase should return mapped flow of products`() = runTest {
        val entities = listOf(
            ProductsEntity(
                productId = "p1",
                storeId = "s1",
                categoryId = "c1",
                brandId = "b1",
                detailTicketEntityId = "d1",
                name = "Prod1",
                description = "Desc1",
                priceBuy = 10.0,
                priceSell = 15.0,
                priceExcludingIGV = 12.0,
                stockQuantity = 100.0,
                stockMin = 5.0,
                barcode = "111",
                image = "img",
                lastUpdated = "date",
                isSynced = 1
            )
        )
        every { mockProductDao.getAllProducts() } returns flowOf(entities)

        repository.getAllProductsFromDataBase().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("p1", items[0].productId)
            assertEquals("Prod1", items[0].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchProducts should return mapped flow based on query`() = runTest {
        val query = "Prod"
        val entities = listOf(
            ProductsEntity(
                productId = "p1",
                storeId = "s1",
                categoryId = "c1",
                brandId = "b1",
                detailTicketEntityId = "d1",
                name = "Prod1",
                description = "Desc1",
                priceBuy = 10.0,
                priceSell = 15.0,
                priceExcludingIGV = 12.0,
                stockQuantity = 100.0,
                stockMin = 5.0,
                barcode = "111",
                image = "img",
                lastUpdated = "date",
                isSynced = 1
            )
        )
        every { mockProductDao.searchProducts(query) } returns flowOf(entities)

        repository.searchProducts(query).test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Prod1", items[0].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `findCodeProduct should return mapped product if found`() = runTest {
        val entity = ProductsEntity(
            productId = "p2",
            storeId = "s1",
            categoryId = "c1",
            brandId = "b1",
            detailTicketEntityId = "d1",
            name = "Prod2",
            description = "Desc2",
            priceBuy = 20.0,
            priceSell = 25.0,
            priceExcludingIGV = 22.0,
            stockQuantity = 50.0,
            stockMin = 5.0,
            barcode = "222",
            image = "img",
            lastUpdated = "date",
            isSynced = 0
        )
        every { mockProductDao.findCodeProduct("222") } returns flowOf(entity)

        repository.findCodeProduct("222").test {
            val model = awaitItem()
            assertEquals("p2", model?.productId)
            assertEquals("Prod2", model?.name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `findCodeProduct should return null if not found`() = runTest {
        every { mockProductDao.findCodeProduct("999") } returns flowOf(null)

        repository.findCodeProduct("999").test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveProduct should save to remote and Room as synced`() = runTest {
        val model = ProductModel(
            productId = "p3",
            storeId = "s1",
            categoryId = "c1",
            brandId = "b1",
            detailTicketEntityId = "d1",
            name = "Prod3",
            priceSell = 30.0,
            priceBuy = 25.0,
            priceExcludingIGV = 28.0,
            description = "Desc3",
            image = "img",
            stockQuantity = 10.0,
            barcode = "333",
            lastUpdated = "date",
            stockMin = 5.0,
            isSynced = false
        )

        coEvery { mockRemoteDataSource.productRemoteDataSource.saveProduct(model) } returns true

        repository.saveProduct(model)

        coVerify(exactly = 1) { mockRemoteDataSource.productRemoteDataSource.saveProduct(model) }
        coVerify(exactly = 1) {
            mockProductDao.insertProduct(withArg { entity ->
                assertEquals("p3", entity.productId)
                assertEquals(1, entity.isSynced) // Debería cambiar a true (1) al guardarse exitosamente
            })
        }
    }

    @Test
    fun `saveProduct should save to Room as unsynced if remote fails`() = runTest {
        val model = ProductModel(
            productId = "p4",
            storeId = "s1",
            categoryId = "c1",
            brandId = "b1",
            detailTicketEntityId = "d1",
            name = "Prod4",
            priceSell = 40.0,
            priceBuy = 35.0,
            priceExcludingIGV = 38.0,
            description = "Desc4",
            image = "img",
            stockQuantity = 10.0,
            barcode = "444",
            lastUpdated = "date",
            stockMin = 5.0,
            isSynced = true
        )

        coEvery { mockRemoteDataSource.productRemoteDataSource.saveProduct(model) } returns false

        repository.saveProduct(model)

        coVerify(exactly = 1) { mockRemoteDataSource.productRemoteDataSource.saveProduct(model) }
        coVerify(exactly = 1) {
            mockProductDao.insertProduct(withArg { entity ->
                assertEquals("p4", entity.productId)
                assertEquals(0, entity.isSynced) // Debería cambiar a false (0) al fallar Firebase/Retrofit
            })
        }
    }

    @Test
    fun `deleteProduct should delete from remote and Room`() = runTest {
        val model = ProductModel(productId = "p5", name = "Prod5")
        coEvery { mockRemoteDataSource.productRemoteDataSource.deleteProduct(model) } returns true

        repository.deleteProduct(model)

        coVerify(exactly = 1) { mockRemoteDataSource.productRemoteDataSource.deleteProduct(model) }
        coVerify(exactly = 1) { mockProductDao.deleteProduct(any()) }
    }

    @Test
    fun `deleteProduct should delete from Room even if remote fails`() = runTest {
        val model = ProductModel(productId = "p6", name = "Prod6")
        coEvery { mockRemoteDataSource.productRemoteDataSource.deleteProduct(model) } returns false
        coEvery { mockProductDao.updateProduct(any()) } returns 1

        repository.deleteProduct(model)

        coVerify(exactly = 1) { mockRemoteDataSource.productRemoteDataSource.deleteProduct(model) }
        coVerify(exactly = 1) { 
            mockProductDao.updateProduct(
                withArg {
                    assertEquals(1, it.isDeleted)
                    assertEquals(0, it.isSynced)
                }
            )
        }
    }

    @Test
    fun `syncUnsyncedProducts should sync and update local database`() = runTest {
        val entityUnsynced = ProductsEntity(
            productId = "pUnsynced",
            storeId = "s1",
            categoryId = "c1",
            brandId = "b1",
            detailTicketEntityId = "d1",
            name = "ProdUnsynced",
            description = "Desc",
            priceBuy = 10.0,
            priceSell = 15.0,
            priceExcludingIGV = 12.0,
            stockQuantity = 100.0,
            stockMin = 5.0,
            barcode = "111",
            image = "img",
            lastUpdated = "date",
            isSynced = 0
        )

        coEvery { mockProductDao.getUnsyncedProducts() } returns listOf(entityUnsynced)
        coEvery { mockRemoteDataSource.productRemoteDataSource.saveProduct(any()) } returns true

        repository.syncUnsyncedProducts()

        coVerify(exactly = 1) { mockProductDao.getUnsyncedProducts() }
        coVerify(exactly = 1) { mockRemoteDataSource.productRemoteDataSource.saveProduct(any()) }
        coVerify(exactly = 1) {
            mockProductDao.insertProduct(withArg {
                assertEquals("pUnsynced", it.productId)
                assertEquals(1, it.isSynced) // Confirmamos que lo vuelve true
            })
        }
    }

    @Test
    fun `syncUnsyncedProducts should delete unsynced products`() = runTest {
        val entityUnsynced = ProductsEntity(
            productId = "pUnsynced",
            storeId = "s1",
            categoryId = "c1",
            brandId = "b1",
            detailTicketEntityId = "d1",
            name = "ProdUnsynced",
            description = "Desc",
            priceBuy = 10.0,
            priceSell = 15.0,
            priceExcludingIGV = 12.0,
            stockQuantity = 100.0,
            stockMin = 5.0,
            barcode = "111",
            image = "img",
            lastUpdated = "date",
            isSynced = 0,
            isDeleted = 1
        )
        coEvery { mockProductDao.getPendingDeletedProducts() } returns listOf(entityUnsynced)
        coEvery { mockRemoteDataSource.productRemoteDataSource.deleteProduct(any()) } returns true

        repository.syncUnsyncedProducts()

        coVerify(exactly = 1) { mockProductDao.getPendingDeletedProducts() }
        coVerify(exactly = 1) { mockRemoteDataSource.productRemoteDataSource.deleteProduct(any()) }
        coVerify(exactly = 1) {
            mockProductDao.deleteProduct(withArg {
                assertEquals("pUnsynced", it.productId)
                assertEquals(0, it.isSynced)
            })
        }
    }


    @Test
    fun `syncUnsyncedProducts should do nothing if no unsynced products and no pending deletions`() = runTest {
        coEvery { mockProductDao.getUnsyncedProducts() } returns emptyList()
        coEvery { mockProductDao.getPendingDeletedProducts() } returns emptyList()

        repository.syncUnsyncedProducts()

        coVerify(exactly = 1) { mockProductDao.getUnsyncedProducts() }
        coVerify(exactly = 1) { mockProductDao.getPendingDeletedProducts() }
        coVerify(exactly = 0) { mockRemoteDataSource.productRemoteDataSource.saveProduct(any()) }
        coVerify(exactly = 0) { mockProductDao.insertProduct(any()) }
    }

    @Test
    fun `refreshProductsFromRemote should download categories, brands and products and update Room`() =
        runTest {
            // Arrange
            val remoteCategories = listOf(CategoryModel("cat1", name = "Cat 1"))
            coEvery { mockRemoteDataSource.categoryRemoteDataSource.getCategories() } returns remoteCategories

            val remoteBrands = listOf(BrandModel("brand1", name = "Brand 1"))
            coEvery { mockRemoteDataSource.brandRemoteDataSource.getBrands() } returns remoteBrands

            val remoteProducts = listOf(
                ProductModel(
                    productId = "pRemote1",
                    categoryId = "cat1",
                    brandId = "brand1",
                    name = "Remote Prod"
                )
            )
            coEvery { mockRemoteDataSource.productRemoteDataSource.getProducts() } returns remoteProducts

            // Act
            repository.refreshProductsFromRemote()

            // Assert
            coVerify(exactly = 1) {
                mockCategoryDao.insertCategory(withArg {
                    assertEquals("cat1", it.categoryId)
                })
            }
            coVerify(exactly = 1) {
                mockBrandDao.upsertAll(withArg {
                    assertEquals("brand1", it.brandId)
                })
            }
            coVerify(exactly = 1) { mockProductDao.deleteProductsNotInList(listOf("pRemote1")) }
            coVerify(exactly = 1) {
                mockProductDao.insertProduct(withArg {
                    assertEquals("pRemote1", it.productId)
                    assertEquals(1, it.isSynced)
                })
            }
        }

    @Test
    fun `refreshProductsFromRemote should clear room database if remote product list is empty`() =
        runTest {
            coEvery { mockRemoteDataSource.categoryRemoteDataSource.getCategories() } returns emptyList()
            coEvery { mockRemoteDataSource.brandRemoteDataSource.getBrands() } returns emptyList()
            coEvery { mockRemoteDataSource.productRemoteDataSource.getProducts() } returns emptyList()

            // Act
            repository.refreshProductsFromRemote()

            // Assert
            coVerify(exactly = 1) { mockProductDao.deleteAllProducts() }
            coVerify(exactly = 0) { mockProductDao.deleteProductsNotInList(any()) }
        }

}