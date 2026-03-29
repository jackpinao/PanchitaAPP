package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import com.pinao.panchitaapp.domain.model.ProductModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductMapperTest {

    @Test
    fun `toDomain should map ProductsEntity to ProductModel correctly`() {
        // Given
        val entity = ProductsEntity(
            productId = "prod123",
            storeId = "store1",
            categoryId = "cat1",
            brandId = "brand1",
            detailTicketEntityId = "ticket1",
            name = "Inca Kola 1L",
            priceSell = 5.50,
            priceBuy = 4.00,
            priceExcludingIGV = 4.66,
            description = "Gaseosa peruana",
            image = "url_imagen",
            stockQuantity = 100.0,
            barcode = "123456789",
            lastUpdated = "1672531200000",
            stockMin = 10.0,
            isSynced = 1
        )

        // When
        val model = ProductMapper.toDomain(entity)

        // Then
        assertEquals(entity.productId, model.productId)
        assertEquals(entity.storeId, model.storeId)
        assertEquals(entity.categoryId, model.categoryId)
        assertEquals(entity.brandId, model.brandId)
        assertEquals(entity.detailTicketEntityId, model.detailTicketEntityId)
        assertEquals(entity.name, model.name)
        assertEquals(entity.priceSell, model.priceSell, 0.0)
        assertEquals(entity.priceBuy, model.priceBuy, 0.0)
        assertEquals(entity.priceExcludingIGV, model.priceExcludingIGV, 0.0)
        assertEquals(entity.description, model.description)
        assertEquals(entity.image, model.image)
        assertEquals(entity.stockQuantity, model.stockQuantity, 0.0)
        assertEquals(entity.barcode, model.barcode)
        assertEquals(entity.lastUpdated, model.lastUpdated)
        assertEquals(entity.stockMin, model.stockMin, 0.0)
        assertTrue(model.isSynced) // 1 should map to true
    }

    @Test
    fun `toDomain should map isSynced correctly when 0`() {
        // Given
        val entity = ProductsEntity(
            productId = "prod123",
            name = "Test",
            isSynced = 0, // 0 means false
            storeId = "",
            categoryId = "",
            brandId = "",
            detailTicketEntityId = "",
            description = "",
            priceBuy = 0.0,
            priceSell = 0.0,
            priceExcludingIGV = 0.0,
            stockQuantity = 0.0,
            stockMin = 0.0,
            barcode = "",
            image = "",
            lastUpdated = ""
        )

        // When
        val model = ProductMapper.toDomain(entity)

        // Then
        assertFalse(model.isSynced)
    }

    @Test
    fun `toDatabase should map ProductModel to ProductsEntity correctly`() {
        // Given
        val model = ProductModel(
            productId = "prod123",
            storeId = "store1",
            categoryId = "cat1",
            brandId = "brand1",
            detailTicketEntityId = "ticket1",
            name = "Inca Kola 1L",
            priceSell = 5.50,
            priceBuy = 4.00,
            priceExcludingIGV = 4.66,
            description = "Gaseosa peruana",
            image = "url_imagen",
            stockQuantity = 100.0,
            barcode = "123456789",
            lastUpdated = "1672531200000",
            stockMin = 10.0,
            isSynced = true // true should map to 1
        )

        // When
        val entity = ProductMapper.toDatabase(model)

        // Then
        assertEquals(model.productId, entity.productId)
        assertEquals(model.storeId, entity.storeId)
        assertEquals(model.categoryId, entity.categoryId)
        assertEquals(model.brandId, entity.brandId)
        assertEquals(model.detailTicketEntityId, entity.detailTicketEntityId)
        assertEquals(model.name, entity.name)
        assertEquals(model.priceSell, entity.priceSell, 0.0)
        assertEquals(model.priceBuy, entity.priceBuy, 0.0)
        assertEquals(model.priceExcludingIGV, entity.priceExcludingIGV, 0.0)
        assertEquals(model.description, entity.description)
        assertEquals(model.image, entity.image)
        assertEquals(model.stockQuantity, entity.stockQuantity, 0.0)
        assertEquals(model.barcode, entity.barcode)
        assertEquals(model.lastUpdated, entity.lastUpdated)
        assertEquals(model.stockMin, entity.stockMin, 0.0)
        assertEquals(1, entity.isSynced) 
    }

    @Test
    fun `toDatabase should map isSynced correctly when false`() {
        // Given
        val model = ProductModel(
            productId = "prod123",
            name = "Test",
            isSynced = false // false should map to 0
        )

        // When
        val entity = ProductMapper.toDatabase(model)

        // Then
        assertEquals(0, entity.isSynced)
    }
}