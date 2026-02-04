package com.pinao.panchitaapp.domain.usecase.category

import com.pinao.panchitaapp.domain.model.CategoryModel
import com.pinao.panchitaapp.domain.repository.CategoryRepository

class SaveCategoryUseCase(
    private val repository: CategoryRepository
) {

    suspend operator fun invoke(categoryModel: CategoryModel) =
        repository.saveCategory(categoryModel)
}
