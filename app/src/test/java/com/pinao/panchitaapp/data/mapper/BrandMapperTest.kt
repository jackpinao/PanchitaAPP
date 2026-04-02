package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.BrandEntity
import com.pinao.panchitaapp.domain.model.BrandModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BrandMapperTest {

    @Test
    fun `toDomain should map BrandEntity to BrandModel correctly`() {
        val entity = BrandEntity(
            brandId = "brand_1",
            storeId = "store_1",
            name = "Coca-Cola",
            isSynced = 1
        )

        val model = BrandMapper.toDomain(entity)

        assertEquals(entity.brandId, model.brandId)
        assertEquals(entity.storeId, model.storeId)
        assertEquals(entity.name, model.name)
        assertTrue(model.isSynced)
    }

    @Test
    fun `toDomain should map isSynced correctly when 0`() {
        val entity = BrandEntity(brandId = "b1", storeId = "s1", name = "Test", isSynced = 0)
        val model = BrandMapper.toDomain(entity)
        assertFalse(model.isSynced)
    }

    @Test
    fun `toDatabase should map BrandModel to BrandEntity correctly`() {
        val model = BrandModel(
            brandId = "brand_1",
            storeId = "store_1",
            name = "Coca-Cola",
            isSynced = true
        )

        val entity = BrandMapper.toDatabase(model)

        assertEquals(model.brandId, entity.brandId)
        assertEquals(model.storeId, entity.storeId)
        assertEquals(model.name, entity.name)
        assertEquals(1, entity.isSynced)
    }

    @Test
    fun `toDatabase should map isSynced correctly when false`() {
        val model = BrandModel(brandId = "b1", storeId = "s1", name = "Test", isSynced = false)
        val entity = BrandMapper.toDatabase(model)
        assertEquals(0, entity.isSynced)
    }
}