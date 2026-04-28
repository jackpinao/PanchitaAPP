package com.pinao.panchitaapp.data.source.remote.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.mapper.CategoryMapper
import com.pinao.panchitaapp.data.source.remote.CategoryRemoteDataSource
import com.pinao.panchitaapp.data.source.remote.dto.FirebaseCategoryDto
import com.pinao.panchitaapp.domain.model.CategoryModel
import kotlinx.coroutines.tasks.await

class FirebaseCategoryDataSource(
    firestore: FirebaseFirestore
) : CategoryRemoteDataSource {

    private val categoriesCollection = firestore.collection("category")
    override suspend fun getCategories(): List<CategoryModel> {
        val snapshot = categoriesCollection.get().await()
        return snapshot.toObjects(FirebaseCategoryDto::class.java).map { CategoryMapper.toDomain(it) }
    }

    override suspend fun saveCategory(category: CategoryModel): Boolean {
        return try {
            categoriesCollection.document(category.categoryId).set(CategoryMapper.toCategoryDto(category)).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseCategoryDataSource", "Error saving category", e)
            false
        }
    }

    override suspend fun deleteCategory(categoryModel: CategoryModel): Boolean {
        return try {
            categoriesCollection.document(categoryModel.categoryId).delete().await()
            true
        }catch (e: Exception){
            Log.e("FirebaseCategoryDataSource", "Error delete category", e)
            false
        }
    }
}