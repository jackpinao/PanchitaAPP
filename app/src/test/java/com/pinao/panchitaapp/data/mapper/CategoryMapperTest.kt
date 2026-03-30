package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.CategoryEntity
import com.pinao.panchitaapp.domain.model.CategoryModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryMapperTest {

    @Test
    fun `toDomain should map CategoryEntity to CategoryModel correctly`() {
        // Given
        val entity = CategoryEntity(
            categoryId = "cat_1",
            storeId = "store_1",
            name = "Bebidas",
            revenue = 15.5,
            isSynced = 1
        )

        // When
        val model = CategoryMapper.toDomain(entity)

        // Then
        assertEquals(entity.categoryId, model.categoryId)
        assertEquals(entity.storeId, model.storeId)
        assertEquals(entity.name, model.name)
        assertEquals(entity.revenue, model.revenue, 0.0)
        assertTrue(model.isSynced)
    }

    @Test
    fun `toDomain should map isSynced correctly when 0`() {
        val entity = CategoryEntity(
            categoryId = "cat_2",
            storeId = "store_1",
            name = "Snacks",
            revenue = 10.0,
            isSynced = 0
        )

        val model = CategoryMapper.toDomain(entity)

        assertFalse(model.isSynced)
    }

    @Test
    fun `toDatabase should map CategoryModel to CategoryEntity correctly`() {
        // Given
        val model = CategoryModel(
            categoryId = "cat_1",
            storeId = "store_1",
            name = "Bebidas",
            revenue = 15.5,
            isSynced = true
        )

        // When
        val entity = CategoryMapper.toDatabase(model)

        // Then
        assertEquals(model.categoryId, entity.categoryId)
        assertEquals(model.storeId, entity.storeId)
        assertEquals(model.name, entity.name)
        assertEquals(model.revenue, entity.revenue, 0.0)
        assertEquals(1, entity.isSynced)
    }

    @Test
    fun `toDatabase should map isSynced correctly when false`() {
        val model = CategoryModel(
            categoryId = "cat_2",
            name = "Snacks",
            isSynced = false
        )

        val entity = CategoryMapper.toDatabase(model)

        assertEquals(0, entity.isSynced)
    }
}