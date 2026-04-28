package com.pinao.panchitaapp.data.source.remote.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.mapper.BrandMapper
import com.pinao.panchitaapp.data.source.remote.BrandRemoteDataSource
import com.pinao.panchitaapp.data.source.remote.dto.FirebaseBrandDto
import com.pinao.panchitaapp.domain.model.BrandModel
import kotlinx.coroutines.tasks.await

class FirebaseBrandDataSource(
    firestore: FirebaseFirestore
) : BrandRemoteDataSource {

    private val brandsCollection = firestore.collection("brand")
    override suspend fun getBrands(): List<BrandModel> {
        val snapshot = brandsCollection.get().await()
        return snapshot.toObjects(FirebaseBrandDto::class.java).map { BrandMapper.toDomain(it) }
    }

    override suspend fun saveBrand(brand: BrandModel): Boolean {
        return try {
            brandsCollection.document(brand.brandId).set(BrandMapper.toBrandDto(brand)).await()
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