package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.model.StockEntryModel
import com.pinao.panchitaapp.domain.repository.ProductRepository

class SaveStockEntryUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(model: StockEntryModel) = productRepository.saveStockEntry(model)
}
