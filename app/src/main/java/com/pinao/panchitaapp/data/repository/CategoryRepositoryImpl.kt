package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.dao.CategoryDao
import com.pinao.panchitaapp.data.mapper.CategoryMapper
import com.pinao.panchitaapp.data.source.remote.RemoteDataSource
import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val remoteDataSource: RemoteDataSource
) : CategoryRepository {

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
                val categories = remoteDataSource.categoryRemoteDataSource.getCategories()
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

            var isSync = remoteDataSource.categoryRemoteDataSource.saveCategory(categoryModel)
            if (isSync) {
                categoryModel.isSynced = true
                categoryDao.insertCategory(CategoryMapper.toDatabase(categoryModel))
            } else{
                categoryModel.isSynced = false
                categoryDao.insertCategory(CategoryMapper.toDatabase(categoryModel))
                Log.d("CategoryRepositoryImpl", "Error saving category to Firestore")
            }
        }
    }

    override suspend fun deleteCategory(categoryModel: CategoryModel) {
        withContext(Dispatchers.IO) {
            val isDeleted = remoteDataSource.categoryRemoteDataSource.deleteCategory(categoryModel)
            if (isDeleted) {
                categoryDao.deleteCategory(CategoryMapper.toDatabase(categoryModel))
            } else {
                Log.d("CategoryRepositoryImpl", "Error deleting category from Firestore")
            }
        }
    }
}