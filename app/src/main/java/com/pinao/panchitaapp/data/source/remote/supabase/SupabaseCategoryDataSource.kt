package com.pinao.panchitaapp.data.source.remote.supabase

import android.util.Log
import com.pinao.panchitaapp.data.mapper.CategoryMapper
import com.pinao.panchitaapp.data.source.remote.CategoryRemoteDataSource
import com.pinao.panchitaapp.domain.model.CategoryModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class SupabaseCategoryDataSource(
    private val supabaseClient: SupabaseClient
) : CategoryRemoteDataSource {

    private val table = supabaseClient.postgrest["categories"]

    override suspend fun getCategories(): List<CategoryModel> {
        return try {
            val result = table.select(columns = Columns.ALL)
                .decodeList<com.pinao.panchitaapp.data.source.remote.dto.SupabaseCategoryDto>()
            result.map { CategoryMapper.toDomain(it) }
        } catch (e: Exception) {
            Log.e("SupabaseCategoryDS", "Error fetching categories", e)
            emptyList()
        }
    }

    override suspend fun saveCategory(category: CategoryModel): Boolean {
        return try {
            val dto = CategoryMapper.toSupabaseDto(category)
            table.upsert(dto)
            true
        } catch (e: Exception) {
            Log.e("SupabaseCategoryDS", "Error saving category", e)
            false
        }
    }

    override suspend fun deleteCategory(categoryModel: CategoryModel): Boolean {
        return try {
            table.delete {
                filter {
                    eq("id", categoryModel.categoryId)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("SupabaseCategoryDS", "Error deleting category", e)
            false
        }
    }
}
