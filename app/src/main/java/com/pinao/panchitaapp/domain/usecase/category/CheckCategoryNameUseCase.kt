package com.pinao.panchitaapp.domain.usecase.category

import com.pinao.panchitaapp.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.first

/**
 * Caso de uso para verificar si un nombre de categoría ya existe.
 */
class CheckCategoryNameUseCase(
    private val categoryRepository: CategoryRepository
) {
    /**
     * Verifica si un nombre de categoría ya existe, ignorando mayúsculas y minúsculas.
     * @param name El nombre de la categoría a verificar.
     * @return `true` si el nombre ya existe, `false` en caso contrario.
     */
    suspend operator fun invoke(name: String): Boolean {
        // Obtenemos la lista actual de categorías una sola vez.
        val currentCategories = categoryRepository.getAllCategoriesFromDataBase().first()
        // Buscamos si alguna categoría tiene el mismo nombre, ignorando el caso.
        return currentCategories.any { it.name.equals(name, ignoreCase = true) }
    }
}