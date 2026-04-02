package com.pinao.panchitaapp.data.mapper

import com.pinao.panchitaapp.data.source.local.entity.SupplierEntity
import com.pinao.panchitaapp.domain.model.SupplierModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SupplierMapperTest {

    @Test
    fun `toDomain should map SupplierEntity to SupplierModel correctly`() {
        val entity = SupplierEntity(
            supplierId = "sup_1",
            storeId = "store_1",
            name = "Distribuidora XYZ",
            phone = "999888777",
            email = "contacto@xyz.com",
            isSynced = 1
        )

        val model = SupplierMapper.toDomain(entity)

        assertEquals(entity.supplierId, model.id)
        assertEquals(entity.storeId, model.storeId)
        assertEquals(entity.name, model.name)
        assertEquals(entity.phone, model.phone)
        assertEquals(entity.email, model.email)
        assertTrue(model.isSynced)
    }

    @Test
    fun `toDomain should map isSynced correctly when 0`() {
        val entity = SupplierEntity(
            supplierId = "sup_1", storeId = "store_1", name = "Dist",
            phone = "123", email = "email", isSynced = 0
        )
        val model = SupplierMapper.toDomain(entity)
        assertFalse(model.isSynced)
    }

    @Test
    fun `toDatabase should map SupplierModel to SupplierEntity correctly`() {
        val model = SupplierModel(
            id = "sup_1",
            storeId = "store_1",
            name = "Distribuidora XYZ",
            phone = "999888777",
            email = "contacto@xyz.com",
            isSynced = true
        )

        val entity = SupplierMapper.toDatabase(model)

        assertEquals(model.id, entity.supplierId)
        assertEquals(model.storeId, entity.storeId)
        assertEquals(model.name, entity.name)
        assertEquals(model.phone, entity.phone)
        assertEquals(model.email, entity.email)
        assertEquals(1, entity.isSynced)
    }

    @Test
    fun `toDatabase should map isSynced correctly when false`() {
        val model = SupplierModel(
            id = "sup_1", storeId = "store_1", name = "Dist",
            phone = "123", email = "email", isSynced = false
        )
        val entity = SupplierMapper.toDatabase(model)
        assertEquals(0, entity.isSynced)
    }
}