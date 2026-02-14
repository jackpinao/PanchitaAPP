package com.pinao.panchitaapp.data.local.dao

import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.pinao.panchitaapp.data.local.database.AppDatabase
import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import kotlinx.coroutines.runBlocking
//import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

//    private lateinit var db: AppDatabase
//    private lateinit var productDao: ProductDao
//
//    @Before
//    fun setUp() {
//        db = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            AppDatabase::class.java
//        ).allowMainThreadQueries().build()
//        productDao = db.productDao()
//    }
//
//    @After
//    fun tearDown() {
//        db.close()
//    }
//
//    @Test
//    fun insertProduct_retrievesProductByCode() = runBlocking {
//        val productEntity = ProductsEntity(
//            productId = "asd",
//            categoryId = "123",
//            detailTicketEntityId = "1",
//            name = "Test Product",
//            description = "Test Description",
//            priceSell = 10.0,
//            stockQuantity = 100.0,
//            barcode = "TESTCODE123",
//            image = "test_image.png"
//        )
//        productDao.insertProduct(productEntity)
//
//        val retrievedProduct = productDao.getProductForCode("TESTCODE123")
//        assertThat(retrievedProduct).isNotNull()
//        assertThat(retrievedProduct?.barcode).isEqualTo("TESTCODE123")
//        assertThat(retrievedProduct?.name).isEqualTo("Test Product")
//    }
//
//    @Test
//    fun insertProduct_withExistingCode_fails() = runBlocking {
//        val product1 = ProductsEntity("qwe", "123","1", "Product 1", "Desc 1", 10.0, 10.0, "CODE1", "")
//        productDao.insertProduct(product1)
//
//        // Attempt to insert another product with the same code
//        // This should ideally throw an exception or the insert should be ignored
//        // depending on the conflict strategy.
//        // For this example, assuming Room's default OnConflictStrategy.ABORT,
//        // which would throw an SQLiteConstraintException.
//        // If your strategy is IGNORE, the second insert will be ignored and getProductForCode will still return product1.
//        // If your strategy is REPLACE, the second insert will replace product1.
//
//        // Let's assume OnConflictStrategy.ABORT for demonstration.
//        // We'll catch the expected exception.
//        var exceptionThrown = false
//        try {
//            val product2 = ProductsEntity("0", "2","2", "Product 2", "Desc 2", 20.0, 20.0, "CODE1", "")
//            productDao.insertProduct(product2)
//        } catch (e: android.database.sqlite.SQLiteConstraintException) {
//            exceptionThrown = true
//            Log.e("ProductDaoTest", "SQLiteConstraintException caught: ${e.message}")
//        }
//        assertThat(exceptionThrown).isTrue()
//
//        // Verify that only the first product is in the database
//        val retrievedProduct = productDao.getProductForCode("CODE1")
//        assertThat(retrievedProduct).isNotNull()
//        assertThat(retrievedProduct?.name).isEqualTo("Product 1")
//    }
}