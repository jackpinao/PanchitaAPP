package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.local.dao.BrandDao
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.mapper.BrandMapper
import com.pinao.panchitaapp.data.mapper.CategoryMapper
import com.pinao.panchitaapp.data.mapper.ProductMapper
import com.pinao.panchitaapp.domain.model.BrandModel
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductsRepositoryImpl(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val brandDao: BrandDao,
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val productsCollection = firestore.collection("product")
    private val categoriesCollection = firestore.collection("category")
    private val brandsCollection = firestore.collection("brand")

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
                val categorySnapshot = categoriesCollection.get().await()
                val categories = categorySnapshot.toObjects(CategoryModel::class.java)
                val categoryIds = categories.map { it.categoryId }.toSet()

                categories.forEach { categoryModel ->
                    categoryDao.insertCategory(CategoryMapper.toDatabase(categoryModel))
                }

                val brandSnapshot = brandsCollection.get().await()
                val brands = brandSnapshot.toObjects(BrandModel::class.java)
                val brandIds = brands.map { it.brandId }.toSet()

                brands.forEach { brandModel ->
                    brandDao.upsertAll(BrandMapper.toDatabase(brandModel))
                }

                val snapshot = productsCollection.get().await()
                val remoteProducts = snapshot.toObjects(ProductModel::class.java)

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
            var isSynced = false
            try {
                // Try to save to Firestore
                productsCollection.document(productModel.productId).set(productModel).await()
                isSynced = true
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error saving to Firestore, saving locally as unsynced", e)
                isSynced = false
            }

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
                    try {
                        productsCollection.document(productModel.productId).set(productModel).await()
                        // Update local status to synced
                        productDao.insertProduct(ProductMapper.toDatabase(productModel.copy(isSynced = true)))
                        Log.d("ProductsRepositoryImpl", "Product ${productModel.name} synced successfully")
                    } catch (e: Exception) {
                        Log.e("ProductsRepositoryImpl", "Failed to sync product ${productModel.name}", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error in syncUnsyncedProducts", e)
            }
        }
    }

    override suspend fun deleteProduct(productModel: ProductModel) {
        withContext(Dispatchers.IO) {
            try {
                productsCollection.document(productModel.productId).delete().await()
                productDao.deleteProduct(ProductMapper.toDatabase(productModel))
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error deleting product", e)
                // If remote delete fails, we might still want to delete locally or mark for deletion
                productDao.deleteProduct(ProductMapper.toDatabase(productModel))
            }
        }
    }
}
