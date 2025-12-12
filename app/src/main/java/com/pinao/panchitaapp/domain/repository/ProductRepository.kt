package com.pinao.panchitaapp.domain.repository

import kotlinx.coroutines.flow.Flow
import com.pinao.panchitaapp.domain.model.ProductModel

interface ProductRepository {

    fun getAllProductsFromDataBase(): Flow<List<ProductModel>>
    suspend fun getAllProductsRemote()
    fun findCodeProduct(codeProduct: String): Flow<ProductModel?>
    suspend fun saveProduct(productModel: ProductModel)
    suspend fun deleteProduct(productModel: ProductModel)
}
