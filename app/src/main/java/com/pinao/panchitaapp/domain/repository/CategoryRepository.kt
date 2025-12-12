package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.CategoryModel
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    /**
     * Obtiene todas las categorías desde la base de datos local (Room).
     * La UI debe observar este Flow.
     */
    fun getAllCategoriesFromDataBase(): Flow<List<CategoryModel>>

    /**
     * Busca las categorías más recientes desde Firestore y las guarda en la base de datos local.
     */
    suspend fun refreshCategoriesFromRemote()

    /**
     * Guarda una nueva categoría tanto en Firestore como en la base de datos local.
     */
    suspend fun saveCategory(categoryModel: CategoryModel)

    /**
     * Elimina una categoría tanto de Firestore como de la base de datos local.
     */
    suspend fun deleteCategory(categoryModel: CategoryModel)
}