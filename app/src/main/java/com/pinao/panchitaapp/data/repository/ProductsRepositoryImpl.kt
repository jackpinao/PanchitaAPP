package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.pinao.panchitaapp.data.mapper.BrandMapper
import com.pinao.panchitaapp.data.mapper.CategoryMapper
import com.pinao.panchitaapp.data.mapper.ProductMapper
import com.pinao.panchitaapp.data.source.local.dao.BrandDao
import com.pinao.panchitaapp.data.source.local.dao.CategoryDao
import com.pinao.panchitaapp.data.source.local.dao.ProductDao
import com.pinao.panchitaapp.data.source.remote.RemoteDataSource
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProductsRepositoryImpl(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val brandDao: BrandDao,
    private val remoteDataSource: RemoteDataSource
) : ProductRepository {

    override fun getAllProductsFromDataBase(): Flow<List<ProductModel>> {
        Log.d("ProductsRepositoryImpl", "Getting all products from Room")
        return productDao.getAllProducts().map { items ->
            items.map { ProductMapper.toDomain(it) }
        }
    }

    override fun searchProducts(query: String): Flow<List<ProductModel>> {
        return productDao.searchProducts(query).map { items ->
            items.map { ProductMapper.toDomain(it) }
        }
    }

    override suspend fun refreshProductsFromRemote() {
        withContext(Dispatchers.IO) {
            try {

                val categories = remoteDataSource.categoryRemoteDataSource.getCategories()
                val categoryIds = categories.map { it.categoryId }.toSet()

                categories.forEach { categoryModel ->
                    categoryDao.insertCategory(CategoryMapper.toDatabase(categoryModel))
                }

                val brands = remoteDataSource.brandRemoteDataSource.getBrands()
                val brandIds = brands.map { it.brandId }.toSet()

                brands.forEach { brandModel ->
                    brandDao.upsertAll(BrandMapper.toDatabase(brandModel))
                }

                val remoteProducts = remoteDataSource.productRemoteDataSource.getProducts()

                if (remoteProducts.isEmpty()) {
                    productDao.deleteAllProducts()
                    return@withContext
                }

                val validRemoteProducts = remoteProducts.filter { product ->
                    categoryIds.contains(product.categoryId) && brandIds.contains(product.brandId)
                }
                val remoteIds = validRemoteProducts.map { it.productId }

                productDao.deleteProductsNotInList(remoteIds)

                validRemoteProducts.forEach { productModel ->
                    // Mark products coming from remote as synced
                    val entity = ProductMapper.toDatabase(productModel.copy(isSynced = true))
                    productDao.insertProduct(entity)
                }
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error during synchronization", e)
            }
        }
    }

    override fun findCodeProduct(codeProduct: String): Flow<ProductModel?> {
        return productDao.findCodeProduct(codeProduct).map { productEntity ->
            productEntity?.let { ProductMapper.toDomain(it) }
        }
    }

    override suspend fun saveProduct(productModel: ProductModel) {
        withContext(Dispatchers.IO) {
            val isSynced = remoteDataSource.productRemoteDataSource.saveProduct(productModel)

            // Save to Room with the calculated sync status
            val productToSave = productModel.copy(isSynced = isSynced)
            productDao.insertProduct(ProductMapper.toDatabase(productToSave))
        }
    }

    override suspend fun syncUnsyncedProducts() {
        withContext(Dispatchers.IO) {
            try {
                val unsyncedEntities = productDao.getUnsyncedProducts()
                if (unsyncedEntities.isEmpty()) return@withContext

                unsyncedEntities.forEach { entity ->
                    val productModel = ProductMapper.toDomain(entity)

                    val isSynced =
                        remoteDataSource.productRemoteDataSource.saveProduct(productModel)

                    if (isSynced) {
                        productDao.insertProduct(ProductMapper.toDatabase(productModel.copy(isSynced = true)))
                        Log.d(
                            "ProductsRepositoryImpl",
                            "Product ${productModel.name} synced successfully"
                        )
                    } else {
                        Log.w(
                            "ProductsRepositoryImpl",
                            "Failed to sync product ${productModel.name}",
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error in syncUnsyncedProducts", e)
            }
        }
    }

    override suspend fun deleteProduct(productModel: ProductModel) {
        withContext(Dispatchers.IO) {
            val isDelete = remoteDataSource.productRemoteDataSource.deleteProduct(productModel)
            if (isDelete) {
                productDao.deleteProduct(ProductMapper.toDatabase(productModel))
            } else {
                Log.d("ProductsRepositoryImpl", "Error deleting product from Firestore")
            }
        }
    }
}
