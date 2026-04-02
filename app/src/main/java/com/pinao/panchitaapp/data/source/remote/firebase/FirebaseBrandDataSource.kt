package com.pinao.panchitaapp.data.source.remote.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.remote.BrandRemoteDataSource
import com.pinao.panchitaapp.domain.model.BrandModel
import kotlinx.coroutines.tasks.await

class FirebaseBrandDataSource(
    firestore: FirebaseFirestore
) : BrandRemoteDataSource {

    private val brandsCollection = firestore.collection("brand")
    override suspend fun getBrands(): List<BrandModel> {
        val snapshot = brandsCollection.get().await()
        return snapshot.toObjects(BrandModel::class.java)
    }

    override suspend fun saveBrand(brand: BrandModel): Boolean {
        return try {
            brandsCollection.document(brand.brandId).set(brand).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseBrandDataSource", "Error saving brand", e)
            false
        }
    }

    override suspend fun deleteBrand(brand: BrandModel): Boolean {
        return try {
            brandsCollection.document(brand.brandId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseBrandDataSource", "Error deleting brand", e)
            false
        }
    }
}