package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.SaleEntity
import com.pinao.panchitaapp.domain.model.SaleModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SaleMapperTest {

    @Test
    fun `toDomain should map SaleEntity to SaleModel correctly`() {
        val entity = SaleEntity(
            saleId = "sale_1",
            storeId = "store_1",
            userId = "user_1",
            clientId = "client_1",
            saleDate = "2023-10-25",
            totalAmount = 150.5,
            paymentType = "CASH",
            isSynced = 1
        )

        val model = SaleMapper.toDomain(entity)

        assertEquals(entity.saleId, model.saleId)
        assertEquals(entity.storeId, model.storeId)
        assertEquals(entity.userId, model.userId)
        assertEquals(entity.clientId, model.clientId)
        assertEquals(entity.saleDate, model.saleDate)
        assertEquals(entity.totalAmount, model.totalAmount, 0.0)
        assertEquals(entity.paymentType, model.paymentType)
        assertTrue(model.isSynced)
    }

    @Test
    fun `toDomain should map isSynced correctly when 0`() {
        val entity = SaleEntity(
            saleId = "s1", storeId = "st1", userId = "u1", clientId = "c1",
            saleDate = "2023-10-25", totalAmount = 10.0, paymentType = "CARD",
            isSynced = 0
        )
        val model = SaleMapper.toDomain(entity)
        assertFalse(model.isSynced)
    }

    @Test
    fun `toEntity should map SaleModel to SaleEntity correctly`() {
        val model = SaleModel(
            saleId = "sale_1",
            storeId = "store_1",
            userId = "user_1",
            clientId = "client_1",
            saleDate = "2023-10-25",
            totalAmount = 150.5,
            paymentType = "CASH",
            isSynced = true
        )

        val entity = SaleMapper.toEntity(model)

        assertEquals(model.saleId, entity.saleId)
        assertEquals(model.storeId, entity.storeId)
        assertEquals(model.userId, entity.userId)
        assertEquals(model.clientId, entity.clientId)
        assertEquals(model.saleDate, entity.saleDate)
        assertEquals(model.totalAmount, entity.totalAmount, 0.0)
        assertEquals(model.paymentType, entity.paymentType)
        assertEquals(1, entity.isSynced)
    }

    @Test
    fun `toEntity should map isSynced correctly when false`() {
        val model = SaleModel(
            saleId = "s1", storeId = "st1", userId = "u1", clientId = "c1",
            saleDate = "2023-10-25", totalAmount = 10.0, paymentType = "CARD",
            isSynced = false
        )
        val entity = SaleMapper.toEntity(model)
        assertEquals(0, entity.isSynced)
    }
}