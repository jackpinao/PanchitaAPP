package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.mapper.ProductMapper
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductsRepositoryImpl(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val productsCollection = firestore.collection("product")

    override fun getAllProductsFromDataBase(): Flow<List<ProductModel>> {
        Log.d("ProductsRepositoryImpl", "Getting all products from Room")
        // La UI siempre observa Room. Room es la fuente única de verdad.
        return productDao.getAllProducts().map { items ->
            items.map { productEntity ->
                ProductMapper.toDomain(productEntity)
            }
        }
    }

    override suspend fun refreshProductsFromRemote() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("ProductsRepositoryImpl", "Fetching products from Firestore")
                val snapshot = productsCollection.get().await()
                val products = snapshot.toObjects(ProductModel::class.java)
                Log.d("ProductsRepositoryImpl", "Got ${products.size} products from Firestore")

                // Una vez obtenidos los datos de Firestore, los guardamos en Room.
                products.forEach { productModel ->
                    productDao.insertProduct(ProductMapper.toDatabase(productModel))
                }
                Log.d("ProductsRepositoryImpl", "Finished saving remote products to Room")
            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error fetching from Firestore", e)
                // Manejar el error, por ejemplo, no borrando la caché local
            }
        }
    }

    override fun findCodeProduct(codeProduct: String): Flow<ProductModel?> {
        Log.d("ProductsRepositoryImpl", "Finding product with code: $codeProduct")
        return productDao.findCodeProduct(codeProduct).map { productEntity ->
            productEntity?.let { ProductMapper.toDomain(it) }
        }
    }

    override suspend fun saveProduct(productModel: ProductModel) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("ProductsRepositoryImpl", "Saving product to Firestore: $productModel")
                // 1. Guardar en Firestore primero
                productsCollection.document(productModel.id).set(productModel).await()

                // 2. Si Firestore tuvo éxito, guardar en Room
                Log.d("ProductsRepositoryImpl", "Saving product to Room: $productModel")
                productDao.insertProduct(ProductMapper.toDatabase(productModel))
                Log.d("ProductsRepositoryImpl", "Product saved successfully in both sources")

            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error saving product", e)
                // Aquí puedes decidir qué hacer si falla. ¿Intentar de nuevo? ¿Notificar al usuario?
            }
        }
    }

    override suspend fun deleteProduct(productModel: ProductModel) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("ProductsRepositoryImpl", "Deleting product from Firestore: $productModel")
                // 1. Borrar de Firestore primero
                productsCollection.document(productModel.id).delete().await()

                // 2. Si Firestore tuvo éxito, borrar de Room
                Log.d("ProductsRepositoryImpl", "Deleting product from Room: $productModel")
                productDao.deleteProduct(ProductMapper.toDatabase(productModel))
                Log.d("ProductsRepositoryImpl", "Product deleted successfully from both sources")

            } catch (e: Exception) {
                Log.e("ProductsRepositoryImpl", "Error deleting product", e)
            }
        }
    }
}