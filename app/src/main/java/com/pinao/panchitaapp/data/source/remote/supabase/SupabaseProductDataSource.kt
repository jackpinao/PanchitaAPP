package com.pinao.panchitaapp.data.source.remote.supabase

import android.util.Log
import com.pinao.panchitaapp.data.mapper.ProductMapper
import com.pinao.panchitaapp.data.source.remote.ProductRemoteDataSource
import com.pinao.panchitaapp.domain.model.ProductModel
import io.github.jan_tennert.supabase.SupabaseClient
import io.github.jan_tennert.supabase.postgrest.postgrest
import io.github.jan_tennert.supabase.postgrest.query.Columns

class SupabaseProductDataSource(
    private val supabaseClient: SupabaseClient
) : ProductRemoteDataSource {

    private val table = supabaseClient.postgrest["products"]

    override suspend fun getProducts(): List<ProductModel> {
        return try {
            val result = table.select(columns = Columns.ALL).decodeList<com.pinao.panchitaapp.data.source.remote.dto.SupabaseProductDto>()
            result.map { ProductMapper.toDomain(it) }
        } catch (e: Exception) {
            Log.e("SupabaseProductDS", "Error fetching products", e)
            emptyList()
        }
    }

    override suspend fun saveProduct(product: ProductModel): Boolean {
        return try {
            val dto = ProductMapper.toSupabaseDto(product)
            table.upsert(dto)
            true
        } catch (e: Exception) {
            Log.e("SupabaseProductDS", "Error saving product", e)
            false
        }
    }

    override suspend fun deleteProduct(productModel: ProductModel): Boolean {
        return try {
            // Soft delete o hard delete dependiendo de la lógica de negocio
            // Usaremos soft delete ya que el DTO tiene isDeleted
            val dto = ProductMapper.toSupabaseDto(productModel.copy(isDeleted = true))
            table.upsert(dto)
            true
        } catch (e: Exception) {
            Log.e("SupabaseProductDS", "Error deleting product", e)
            false
        }
    }
}
