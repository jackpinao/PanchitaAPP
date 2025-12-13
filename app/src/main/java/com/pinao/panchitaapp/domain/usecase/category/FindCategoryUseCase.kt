package com.pinao.panchitaapp.domain.usecase.category

import com.pinao.panchitaapp.domain.repository.CategoryRepository

class FindCategoryUseCase(
    private val repository: CategoryRepository
) {
    operator fun invoke(codeCategory: String) = repository.findCodeCategory(codeCategory)
}
