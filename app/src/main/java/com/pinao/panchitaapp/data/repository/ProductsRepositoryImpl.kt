package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.mapper.CategoryMapper
import com.pinao.panchitaapp.data.mapper.ProductMapper
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
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val productsCollection = firestore.collection("product")
    private val categoriesCollection = firestore.collection("category")

    override fun getAllProductsFromDataBase(): Flow<List<ProductModel>> {
        Log.d("ProductsRepositoryImpl", "Getting all products from Room")
        // La UI siempre observa Room. Room es la fuente única de verdad.
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
                Log.d("ProductsRepositoryImpl", "Refreshing categories first to avoid FK constraints")
                // 1. Fetch and Sync Categories first
                val categorySnapshot = categoriesCollection.get().await()
                val categories = categorySnapshot.toObjects(CategoryModel::class.java)

                // We keep track of the category IDs that exist in Firestore/Local
                val categoryIds = categories.map { it.id }.toSet()
                
                categories.forEach { categoryModel ->
                    categoryDao.insertCategory(CategoryMapper.toDatabase(categoryModel))
                }

                Log.d("ProductsRepositoryImpl", "Fetching products from Firestore")
                // 2. Fetch and Sync Products
                val snapshot = productsCollection.get().await()
                val products = snapshot.toObjects(ProductModel::class.java)
                Log.d("ProductsRepositoryImpl", "Got ${products.size} products from Firestore")

                // Filter products that have a valid category ID to avoid SQLiteConstraintException
                val validProducts = products.filter { product ->
                    categoryIds.contains(product.idCategory)
                }

                // Una vez obtenidos los datos de Firestore, los guardamos en Room.
                validProducts.forEach { productModel ->
                    productDao.insertProduct(ProductMapper.toDatabase(productModel))
                }
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error fetching from Firestore", e)
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
            try {
                productsCollection.document(productModel.id).set(productModel).await()
                productDao.insertProduct(ProductMapper.toDatabase(productModel))
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error saving product", e)
            }
        }
    }

    override suspend fun deleteProduct(productModel: ProductModel) {
        withContext(Dispatchers.IO) {
            try {
                productsCollection.document(productModel.id).delete().await()
                productDao.deleteProduct(ProductMapper.toDatabase(productModel))
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error deleting product", e)
            }
        }
    }
}