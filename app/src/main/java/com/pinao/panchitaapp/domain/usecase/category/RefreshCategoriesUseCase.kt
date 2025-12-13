package com.pinao.panchitaapp.domain.usecase.category

import com.pinao.panchitaapp.domain.repository.CategoryRepository

/**
 * Caso de uso para forzar la actualización de las categorías desde la fuente remota (Firestore).
 */
class RefreshCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke() {
        categoryRepository.refreshCategoriesFromRemote()
    }
}