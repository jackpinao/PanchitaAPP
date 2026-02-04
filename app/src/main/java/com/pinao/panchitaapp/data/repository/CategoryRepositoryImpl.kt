package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.mapper.CategoryMapper
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    private val categoriesCollection = firestore.collection("category")

    override fun getAllCategoriesFromDataBase(): Flow<List<CategoryModel>> {
        Log.d("CategoryRepositoryImpl", "Getting all categories from Room")
        return categoryDao.getAllCategories().map { items ->
            items.map { categoryEntity ->
                CategoryMapper.toDomain(categoryEntity)
            }
        }
    }

    override suspend fun refreshCategoriesFromRemote() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("CategoryRepositoryImpl", "Fetching categories from Firestore")
                val snapshot = categoriesCollection.get().await()
                val categories = snapshot.toObjects(CategoryModel::class.java)
                Log.d("CategoryRepositoryImpl", "Got ${categories.size} categories from Firestore")

                // Guardar las categorías obtenidas en Room
                categories.forEach { categoryModel ->
                    categoryDao.insertCategory(CategoryMapper.toDatabase(categoryModel))
                }
                Log.d("CategoryRepositoryImpl", "Finished saving remote categories to Room")
            } catch (e: Exception) {
                Log.e("CategoryRepositoryImpl", "Error fetching categories from Firestore", e)
            }
        }
    }

    override fun findCodeCategory(codeCategory: String): Flow<CategoryModel?> {
        Log.d("CategoryRepositoryImpl", "Finding category with code: $codeCategory")
        return categoryDao.findCodeCategory(codeCategory).map { categoryEntity ->
            categoryEntity?.let { CategoryMapper.toDomain(it) }
        }
    }

    override suspend fun saveCategory(categoryModel: CategoryModel) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("CategoryRepositoryImpl", "Saving category to Firestore: $categoryModel")
                // 1. Guardar en Firestore
                categoriesCollection.document(categoryModel.id).set(categoryModel).await()

                // 2. Guardar en Room
                Log.d("CategoryRepositoryImpl", "Saving category to Room: $categoryModel")
                categoryDao.insertCategory(CategoryMapper.toDatabase(categoryModel))
                Log.d("CategoryRepositoryImpl", "Category saved successfully in both sources")

            } catch (e: Exception) {
                Log.e("CategoryRepositoryImpl", "Error saving category", e)
            }
        }
    }

    override suspend fun deleteCategory(categoryModel: CategoryModel) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("CategoryRepositoryImpl", "Deleting category from Firestore: $categoryModel")
                // 1. Borrar de Firestore
                categoriesCollection.document(categoryModel.id).delete().await()

                // 2. Borrar de Room
                Log.d("CategoryRepositoryImpl", "Deleting category from Room: $categoryModel")
                categoryDao.deleteCategory(CategoryMapper.toDatabase(categoryModel))
                Log.d("CategoryRepositoryImpl", "Category deleted successfully from both sources")

            } catch (e: Exception) {
                Log.e("CategoryRepositoryImpl", "Error deleting category", e)
            }
        }
    }
}