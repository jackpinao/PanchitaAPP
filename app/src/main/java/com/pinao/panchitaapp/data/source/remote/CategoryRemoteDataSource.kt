package com.pinao.panchitaapp.data.source.remote

import com.pinao.panchitaapp.domain.model.CategoryModel

interface CategoryRemoteDataSource {
    suspend fun getCategories(): List<CategoryModel>
    suspend fun saveCategory(category: CategoryModel): Boolean
    suspend fun deleteCategory(categoryModel: CategoryModel): Boolean
}