package com.pinao.panchitaapp.data.source.remote

import com.pinao.panchitaapp.domain.model.ProductModel

interface ProductRemoteDataSource {
    suspend fun getProducts(): List<ProductModel>
    suspend fun saveProduct(product: ProductModel): Boolean
    suspend fun deleteProduct(productModel: ProductModel): Boolean
}