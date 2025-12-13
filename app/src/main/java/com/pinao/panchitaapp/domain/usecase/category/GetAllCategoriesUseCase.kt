package com.pinao.panchitaapp.domain.usecase.category

import com.pinao.panchitaapp.domain.repository.CategoryRepository

class GetAllCategoriesUseCase(
    private val repository: CategoryRepository
) {
    operator fun invoke() = repository.getAllCategoriesFromDataBase()

}
