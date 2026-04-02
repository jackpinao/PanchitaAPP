package com.pinao.panchitaapp.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pinao.panchitaapp.data.source.local.database.AppDatabase
import com.pinao.panchitaapp.data.source.local.entity.BrandEntity
import com.pinao.panchitaapp.data.source.local.entity.CategoryEntity
import com.pinao.panchitaapp.data.source.local.dao.BrandDao
import com.pinao.panchitaapp.data.source.local.entity.ProductsEntity
import com.pinao.panchitaapp.data.source.local.dao.CategoryDao
import com.pinao.panchitaapp.data.source.local.dao.ProductDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var productDao: ProductDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var brandDao: BrandDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        productDao = database.productDao()
        categoryDao = database.categoryDao()
        brandDao = database.brandDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertProduct_and_read_it_in_list() = runTest {
        val category = CategoryEntity("cat1", "store1", "Lácteos", 10.0)
        val brand = BrandEntity("brand1", "store1", "Gloria")
        categoryDao.insertCategory(category)
        brandDao.upsertAll(brand)

        val product = ProductsEntity(
            productId = "prod1",
            storeId = "store1",
            categoryId = "cat1",
            brandId = "brand1",
            detailTicketEntityId = "",
            name = "Leche Gloria",
            description = "Tarro azul",
            priceBuy = 3.50,
            priceSell = 4.20,
            priceExcludingIGV = 3.80,
            stockQuantity = 50.0,
            stockMin = 10.0,
            barcode = "77500000001",
            image = "",
            lastUpdated = "2023-10-27",
            isSynced = 1
        )

        productDao.insertProduct(product)

        val allProducts = productDao.getAllProducts().first()
        assert(allProducts.any { it.productId == "prod1" && it.name == "Leche Gloria" })
    }

    @Test
    fun searchProducts_returns_correct_results() = runTest {
        val category = CategoryEntity("cat1", "store1", "Gaseosas", 10.0)
        val brand = BrandEntity("brand1", "store1", "Coca Cola")
        categoryDao.insertCategory(category)
        brandDao.upsertAll(brand)

        val p1 = ProductsEntity("id1", "s1", "cat1", "brand1", "", "Inka Kola", "3L", 5.0, 7.0, 6.0, 10.0, 2.0, "123", "", "", 1)
        val p2 = ProductsEntity("id2", "s1", "cat1", "brand1", "", "Coca Cola", "3L", 5.0, 7.0, 6.0, 10.0, 2.0, "456", "", "", 1)
        
        productDao.insertProduct(p1)
        productDao.insertProduct(p2)

        val results = productDao.searchProducts("Inka").first()

        assert(results.size == 1)
        assert(results[0].name == "Inka Kola")
    }
}
