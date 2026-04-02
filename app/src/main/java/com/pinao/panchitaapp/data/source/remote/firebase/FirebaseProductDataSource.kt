package com.pinao.panchitaapp.data.source.remote.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.remote.ProductRemoteDataSource
import com.pinao.panchitaapp.domain.model.ProductModel
import kotlinx.coroutines.tasks.await

class FirebaseProductDataSource(
    firestore: FirebaseFirestore
) : ProductRemoteDataSource {

    private val productsCollection = firestore.collection("product")

    override suspend fun getProducts(): List<ProductModel> {
        val snapshot = productsCollection.get().await()
        return snapshot.toObjects(ProductModel::class.java)
    }

    override suspend fun saveProduct(product: ProductModel): Boolean {
        return try {
            productsCollection.document(product.productId).set(product).await()
            true
        } catch (e: Exception){
            Log.e("FirebaseProductDataSource", "Error saving product", e)
            false
        }
    }

    override suspend fun deleteProduct(productModel: ProductModel): Boolean {
        return try {
            productsCollection.document(productModel.productId).delete().await()
            true
        } catch (e: Exception) {
            // Handle the error
            Log.e("FirebaseProductDataSource", "Error deleting product", e)
            false
        }
    }
}