package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.mapper.StockEntryMapper
import com.pinao.panchitaapp.data.source.local.entity.StockEntryEntity
import com.pinao.panchitaapp.domain.model.StockEntryModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StockEntryMapperTest {

    @Test
    fun `toDomain should map StockEntryEntity to StockEntryModel correctly`() {
        val entity = StockEntryEntity(
            entryId = "entry_1",
            storeId = "store_1",
            supplierId = "sup_1",
            entryDate = 1672531200000L,
            totalCost = 1500.0,
            documentNumber = "DOC-001",
            isSynced = 1
        )

        val model = StockEntryMapper.toDomain(entity)

        assertEquals(entity.entryId, model.entryId)
        assertEquals(entity.storeId, model.storeId)
        assertEquals(entity.supplierId, model.supplierId)
        assertEquals(entity.entryDate, model.entryDate)
        assertEquals(entity.totalCost, model.totalCost, 0.0)
        assertEquals(entity.documentNumber, model.documentNumber)
        assertTrue(model.isSynced)
    }

    @Test
    fun `toDomain should map isSynced correctly when 0`() {
        val entity = StockEntryEntity(
            entryId = "entry_1", storeId = "st1", supplierId = "su1",
            entryDate = 1672531200000L, totalCost = 10.0, documentNumber = "doc",
            isSynced = 0
        )
        val model = StockEntryMapper.toDomain(entity)
        assertFalse(model.isSynced)
    }

    @Test
    fun `toDatabase should map StockEntryModel to StockEntryEntity correctly`() {
        val model = StockEntryModel(
            entryId = "entry_1",
            storeId = "store_1",
            supplierId = "sup_1",
            entryDate = 1672531200000L,
            totalCost = 1500.0,
            documentNumber = "DOC-001",
            isSynced = true
        )

        val entity = StockEntryMapper.toDatabase(model)

        assertEquals(model.entryId, entity.entryId)
        assertEquals(model.storeId, entity.storeId)
        assertEquals(model.supplierId, entity.supplierId)
        assertEquals(model.entryDate, entity.entryDate)
        assertEquals(model.totalCost, entity.totalCost, 0.0)
        assertEquals(model.documentNumber, entity.documentNumber)
        assertEquals(1, entity.isSynced)
    }

    @Test
    fun `toDatabase should map isSynced correctly when false`() {
        val model = StockEntryModel(
            entryId = "entry_1", storeId = "st1", supplierId = "su1",
            entryDate = 1672531200000L, totalCost = 10.0, documentNumber = "doc",
            isSynced = false
        )
        val entity = StockEntryMapper.toDatabase(model)
        assertEquals(0, entity.isSynced)
    }
}