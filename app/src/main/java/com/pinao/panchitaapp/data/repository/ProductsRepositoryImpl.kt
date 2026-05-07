package com.pinao.panchitaapp.data.repository

import android.util.Log

import com.pinao.panchitaapp.data.mapper.CategoryMapper
import com.pinao.panchitaapp.data.mapper.ProductMapper
import com.pinao.panchitaapp.data.mapper.StockEntryMapper

import com.pinao.panchitaapp.data.source.local.dao.CategoryDao
import com.pinao.panchitaapp.data.source.local.dao.ProductDao
import com.pinao.panchitaapp.data.source.local.dao.StockEntryDao
import com.pinao.panchitaapp.data.source.remote.RemoteDataSource
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.StockEntryModel
import com.pinao.panchitaapp.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProductsRepositoryImpl(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val stockEntryDao: StockEntryDao,
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
                Log.d("ProductsRepositoryImpl", "Starting manual refresh from Supabase")
                val categories = remoteDataSource.categoryRemoteDataSource.getCategories()
                Log.d("ProductsRepositoryImpl", "Fetched ${categories.size} categories")
                val categoryIds = categories.map { it.categoryId }.toSet()

                categories.forEach { categoryModel ->
                    categoryDao.insertCategory(CategoryMapper.toDatabase(categoryModel))
                }



                val remoteProducts = remoteDataSource.productRemoteDataSource.getProducts()
                Log.d("ProductsRepositoryImpl", "Fetched ${remoteProducts.size} products from Supabase")

                if (remoteProducts.isEmpty()) {
                    Log.d("ProductsRepositoryImpl", "No remote products found, cleaning local database")
                    productDao.deleteAllProducts()
                    return@withContext
                }

                val validRemoteProducts = remoteProducts.filter { product ->
                    val catValid = product.categoryId.isEmpty() || categoryIds.contains(product.categoryId)
                    if (!catValid) Log.w("ProductsRepositoryImpl", "Product ${product.name} has invalid category: ${product.categoryId}")
                    catValid
                }
                
                Log.d("ProductsRepositoryImpl", "Products valid for local storage: ${validRemoteProducts.size}")
                val remoteIds = validRemoteProducts.map { it.productId }

                productDao.deleteProductsNotInList(remoteIds)

                validRemoteProducts.forEach { productModel ->
                    val entity = ProductMapper.toDatabase(productModel.copy(isSynced = true))
                    productDao.insertProduct(entity)
                }
                Log.d("ProductsRepositoryImpl", "Refresh completed successfully")
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

                // Sincronizar (Ejecutar) los borrados lógicos pendientes
                val pendingDeletions = productDao.getPendingDeletedProducts()
                pendingDeletions.forEach { entity ->
                    val productModel = ProductMapper.toDomain(entity)
                    val isDelete = remoteDataSource.productRemoteDataSource.deleteProduct(productModel)
                    
                    if (isDelete) {
                        // Borrado en nube exitoso -> Borrado físico en Room
                        productDao.deleteProduct(entity)
                        Log.d("ProductsRepositoryImpl", "Product ${entity.name} permanently deleted.")
                    } else {
                        Log.w("ProductsRepositoryImpl", "Failed to sync delete for ${entity.name}")
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
                // Borrado físico de la nube exitoso -> borramos localmente
                productDao.deleteProduct(ProductMapper.toDatabase(productModel))
            } else {
                // Falló en la nube -> soft delete en Room (Borrado Lógico)
                val entity = ProductMapper.toDatabase(productModel.copy(isDeleted = true, isSynced = false))
                productDao.updateProduct(entity)
                Log.d("ProductsRepositoryImpl", "Error deleting from Firestore. Marked for deletion later.")
            }
        }
    }

    override suspend fun saveStockEntry(model: StockEntryModel) {
        withContext(Dispatchers.IO) {
            stockEntryDao.upsertStockEntry(StockEntryMapper.toDatabase(model))
        }
    }
}
