package com.pinao.panchitaapp.domain.usecase.temporary

import com.pinao.panchitaapp.domain.model.TemporaryProductModel
import com.pinao.panchitaapp.domain.repository.TemporaryProductRepository
import kotlinx.coroutines.flow.Flow

data class TemporaryProductUseCases(
    val getAll: GetAllTemporaryProductsUseCase,
    val save: SaveTemporaryProductUseCase,
    val delete: DeleteTemporaryProductUseCase,
    val clearAll: ClearTemporaryProductsUseCase
)

class GetAllTemporaryProductsUseCase(private val repository: TemporaryProductRepository) {
    operator fun invoke(): Flow<List<TemporaryProductModel>> = repository.getTemporaryProducts()
}

class SaveTemporaryProductUseCase(private val repository: TemporaryProductRepository) {
    suspend operator fun invoke(item: TemporaryProductModel) = repository.saveTemporaryProduct(item)
}

class DeleteTemporaryProductUseCase(private val repository: TemporaryProductRepository) {
    suspend operator fun invoke(item: TemporaryProductModel) = repository.deleteTemporaryProduct(item)
}

class ClearTemporaryProductsUseCase(private val repository: TemporaryProductRepository) {
    suspend operator fun invoke() = repository.clearAllTemporaryProducts()
}