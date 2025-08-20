package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import com.pinao.panchitaapp.data.mapper.ProductMapper
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

class ProductsRepositoryImpl(
    private val productDao: ProductDao
): ProductRepository {

    override fun getAllProductsFromDataBase(): Flow<List<ProductModel>> {
        Log.d("ProductsRepositoryImpl", "Getting all products")
        return productDao.getAllProducts().map { items ->
            items.map { productEntity ->
                ProductMapper.toDomain(productEntity)
            }
        }
    }

    override fun findCodeProduct(codeProduct: String): Flow<ProductModel?> {
        Log.d("ProductsRepositoryImpl", "Finding product with code: $codeProduct")
        return productDao.findCodeProduct(codeProduct).map { productEntity ->
            productEntity?.let { ProductMapper.toDomain(it) }
        }
    }

    override suspend fun saveProduct(productModel: ProductModel) {

        Log.d("ProductsRepositoryImpl", "Saving product: $productModel")
        val num: Long = productDao.insertProduct(ProductMapper.toDatabase(productModel))
        Log.d("ProductsRepositoryImpl", "Num: $num")
        if (num.toInt() == 0) {
            Log.d("ProductsRepositoryImpl", "Product not inserted")
        } else {
            Log.d("ProductsRepositoryImpl", "Product inserted")
        }
//        val response: Flow<List<ProductsEntity>> = productDao.getAllProducts()

//        val firstListOrNull = response.firstOrNull()
//        if (firstListOrNull?.isNotEmpty() == true) {
//            productDao.updateProduct(ProductMapper.toDatabase(productModel))
//            Log.d("ProductsRepositoryImpl", "Product updated")
//        } else {
//            productDao.insertProduct(ProductMapper.toDatabase(productModel))
//            Log.d("ProductsRepositoryImpl", "Product inserted")
//        }

//        if (response.count() == 0) {
//            productDao.insertProduct(ProductMapper.toDatabase(productModel))
//            Log.d("ProductsRepositoryImpl", "Product inserted")
//        } else {
//            productDao.updateProduct(ProductMapper.toDatabase(productModel))
//            Log.d("ProductsRepositoryImpl", "Product updated")
//        }
    }

    override suspend fun deleteProduct(productModel: ProductModel) {
        Log.d("ProductsRepositoryImpl", "Deleting product: $productModel")
        return productDao.deleteProduct(ProductMapper.toDatabase(productModel))
    }

}