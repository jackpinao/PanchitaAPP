package com.pinao.panchitaapp.domain.repository

import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel

interface SaleRepository {
    suspend fun saveFullSale(ticket: SaleModel, products: List<ProductModel>)
}