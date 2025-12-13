package com.pinao.panchitaapp.domain.usecase.category

data class CategoryUseCases(
    val getAll: GetAllCategoriesUseCase,
    val findByCode: FindCategoryUseCase,
    val save: SaveCategoryUseCase,
    val delete: DeleteCategoryUseCase,
    val refreshCategories: RefreshCategoriesUseCase
)
